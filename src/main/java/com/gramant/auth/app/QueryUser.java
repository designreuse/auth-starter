package com.gramant.auth.app;

import com.gramant.auth.domain.User;
import com.gramant.auth.domain.UserId;
import com.gramant.auth.domain.UserInvariants;
import com.gramant.auth.domain.UserRepository;
import com.gramant.auth.domain.ex.UserMissingException;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

public interface QueryUser {

    User findEnabledByEmail(@NotNull String email) throws UserMissingException;

    User findEnabledById(@NotNull UserId userId) throws UserMissingException;

    Collection<User> list();

    User get(@NotNull UserId userId) throws UserMissingException;


    // default implementation
    @AllArgsConstructor
    class Default implements QueryUser {

        private final UserRepository userRepository;
        private final UserInvariants userInvariants;

        @Override
        public User findEnabledByEmail(@NotNull String email) throws UserMissingException {
            return userRepository.findByEmail(email).orElseThrow(() -> new UserMissingException(email));
        }

        @Override
        public User findEnabledById(@NotNull UserId userId) throws UserMissingException {
            return userRepository.get(userId)
                    .flatMap(user -> user.enabled() ? Optional.of(user) : Optional.empty())
                    .orElseThrow(() -> new UserMissingException(userId));
        }

        @Override
        public Collection<User> list() {
            return userRepository.list();
        }

        @Override
        public User get(@NotNull UserId userId) throws UserMissingException {
            return userInvariants.ensuredExistence(userId);
        }
    }
}
