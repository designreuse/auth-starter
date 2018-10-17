package com.gramant.auth.app;

import com.gramant.auth.domain.User;
import com.gramant.auth.domain.UserId;
import com.gramant.auth.domain.UserRepository;
import com.gramant.auth.domain.ex.UserMissingException;
import com.gramant.auth.ports.rest.request.CommunicationRequest;
import com.gramant.auth.ports.rest.request.UpdateActivityRequest;
import com.gramant.auth.ports.rest.request.UserRegistrationRequest;
import com.gramant.auth.ports.rest.request.UserUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Validated
public class DefaultUserManager implements ManageUser {

    private UserRepository userRepository;
    private PasswordEncoder encoder;
    private Notifier notifier;

    @Override
    public User add(@NotNull @Valid UserRegistrationRequest userRegistrationRequest) {
        User createdUser = userRepository
                .add(userRegistrationRequest.asUserWithMappedPassword(password -> encoder.encode(password)));
        // todo: [#events] UserCreatedEvent
        notifier.registrationSuccess(createdUser);
        return createdUser;
    }

    @Override
    public User update(@NotNull @Valid UserUpdateRequest userUpdateRequest) {
        return null;
    }

    @Override
    public User findEnabledByEmail(@NotNull String email) throws UserMissingException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserMissingException(email));
    }

    @Override
    public User findEnabledById(@NotNull UserId userId) throws UserMissingException {
        return userRepository.get(userId).orElseThrow(() -> new UserMissingException(userId));
    }

    @Override
    public List<User> list() {
        return null;
    }

    @Override
    public User get(@NotNull UserId userId) {
        return null;
    }

    @Override
    @Transactional
    public void batchUpdateActivity(@NotNull @Valid UpdateActivityRequest request, boolean activate) {
        Collection<User> users = userRepository.getAll(request.userIds());

        if (activate) {
            userRepository.updateAll(users.stream().map(User::activated).collect(toList()));
        } else {
            userRepository.updateAll(users.stream().map(User::deactivated).collect(toList()));
        }
    }

    @Override
    public void communicate(@NotNull @Valid CommunicationRequest request) {
        Collection<User> users = userRepository.getAll(request.userIds());

        users.forEach(u -> notifier.communicate(u, request.message()));
    }
}
