package com.gramant.auth.app;

import com.gramant.auth.AuthProperties;
import com.gramant.auth.domain.*;
import com.gramant.auth.domain.event.PasswordResetCompleted;
import com.gramant.auth.domain.event.UserCreatedEvent;
import com.gramant.auth.adapters.rest.request.CommunicationRequest;
import com.gramant.auth.adapters.rest.request.UpdateActivityRequest;
import com.gramant.auth.adapters.rest.request.UserRegistrationRequest;
import com.gramant.auth.adapters.rest.request.UserUpdateRequest;
import com.gramant.auth.domain.event.UsersMessaged;
import com.gramant.auth.domain.ex.UserMissingException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Validated
public class DefaultUserManager implements ManageUser {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleProvider roleProvider;
    private final ApplicationEventPublisher eventPublisher;
    private final AuthProperties authProperties;
    private final VerificationTokenOperations verificationTokenOperations;
    private final UserInvariants userInvariants;
    private final PasswordGenerator passwordGenerator;

    @Override
    public User add(@NotNull @Valid UserRegistrationRequest userRegistrationRequest) {
        User createdUser = userRepository
                .add(userRegistrationRequest.asUserWithMappedPassword(
                        encoder::encode,
                        roleProvider.defaultRole(),
                        !authProperties.getConfirmEmail()));

        if (authProperties.getConfirmEmail()) {
            verificationTokenOperations.requestEmailConfirmation(createdUser);
        }

        eventPublisher.publishEvent(new UserCreatedEvent(createdUser.id(), createdUser.roles(),
                userRegistrationRequest.getAdditionalProperties()));

        return createdUser;
    }

    @Override
    public User update(@NotNull UserId userId, @NotNull @Valid UserUpdateRequest request) throws UserMissingException {
        User user = userInvariants.ensuredExistence(userId)
                .updatedWith(request.getEmail(), request.getNonLocked(), roleProvider.roles(request.getRoles()));
        userRepository.update(user);
        return user;
    }

    @Override
    @Transactional
    public void batchUpdateActivity(@NotNull @Valid UpdateActivityRequest request, boolean activate) {
        Collection<User> users = userRepository.getAll(request.userIds());

        if (activate) {
            userRepository.updateAll(users.stream().map(User::asNonLocked).collect(toList()));
        } else {
            userRepository.updateAll(users.stream().map(User::asLocked).collect(toList()));
        }
    }

    @Override
    public void communicate(@NotNull @Valid CommunicationRequest request) {
        Collection<User> users = userRepository.getAll(request.userIds());

        eventPublisher.publishEvent(new UsersMessaged(users, request.message()));
    }

    @Override
    @Transactional
    public void resetPassword(UserId userId) throws UserMissingException {
        User user = userInvariants.ensuredExistence(userId);
        String newPassword = passwordGenerator.generatePassword();
        User updatedUser = user.withPassword(encoder.encode(newPassword));

        userRepository.update(updatedUser);
        eventPublisher.publishEvent(new PasswordResetCompleted(user, newPassword));
    }
}
