package com.gramant.auth.app;

import com.gramant.auth.AuthProperties;
import com.gramant.auth.domain.User;
import com.gramant.auth.domain.UserRepository;
import com.gramant.auth.domain.event.UserCreatedEvent;
import com.gramant.auth.adapters.rest.request.CommunicationRequest;
import com.gramant.auth.adapters.rest.request.UpdateActivityRequest;
import com.gramant.auth.adapters.rest.request.UserRegistrationRequest;
import com.gramant.auth.adapters.rest.request.UserUpdateRequest;
import com.gramant.auth.domain.ex.RoleMissingException;
import com.gramant.auth.domain.ex.UserMissingException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Validated
public class DefaultUserManager implements ManageUser {

    private UserRepository userRepository;
    private PasswordEncoder encoder;
    private Notifier notifier;
    private RoleProvider roleProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthProperties authProperties;

    @Override
    public User add(@NotNull @Valid UserRegistrationRequest userRegistrationRequest) {
        User createdUser = userRepository
                .add(userRegistrationRequest.asUserWithMappedPassword(
                        password -> encoder.encode(password),
                        roleProvider.defaultRole(),
                        !authProperties.getConfirmEmail()));

        try {

            if (authProperties.getConfirmEmail()) {
                notifier.confirmEmail();
            } else {
                notifier.registrationSuccess(createdUser);
            }
        } catch (UnsupportedOperationException e) {
        }

        eventPublisher.publishEvent(new UserCreatedEvent(createdUser.id(), createdUser.roles(), userRegistrationRequest.getAdditionalProperties()));

        return createdUser;
    }

    @Override
    public User update(@NotNull @Valid UserUpdateRequest request) throws UserMissingException {
        Optional<User> oldUser = userRepository.get(request.getId());
        User user = oldUser.orElseThrow(() -> new UserMissingException(request.getId()))
                .updatedWith(request.getEmail(), request.getEnabled(), request.getRoles().stream()
                        .map(roleId -> roleProvider.role(roleId).orElseThrow(() -> new RoleMissingException(roleId)))
                        .collect(toList()));
        userRepository.update(user);
        return user;
    }

    @Override
    @Transactional
    public void batchUpdateActivity(@NotNull @Valid UpdateActivityRequest request, boolean activate) {
        Collection<User> users = userRepository.getAll(request.userIds());

        if (activate) {
            userRepository.updateAll(users.stream().map(User::asActivated).collect(toList()));
        } else {
            userRepository.updateAll(users.stream().map(User::asDeactivated).collect(toList()));
        }
    }

    @Override
    public void communicate(@NotNull @Valid CommunicationRequest request) {
        Collection<User> users = userRepository.getAll(request.userIds());

        users.forEach(u -> notifier.communicate(u, request.message()));
    }
}
