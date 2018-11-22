package com.gramant.auth.domain;

import com.gramant.auth.domain.ex.UserMissingException;
import lombok.AllArgsConstructor;

// domain service for ensuring user invariants
public interface UserInvariants {

    User ensuredExistence(UserId userId) throws UserMissingException;


    // default implementation
    @AllArgsConstructor
    class Default implements UserInvariants {

        private final UserRepository userRepository;

        @Override
        public User ensuredExistence(UserId userId) throws UserMissingException {
            return userRepository.get(userId).orElseThrow(() -> new UserMissingException(userId));
        }
    }
}
