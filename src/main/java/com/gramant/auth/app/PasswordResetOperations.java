package com.gramant.auth.app;

import com.gramant.auth.domain.*;
import com.gramant.auth.domain.ex.PasswordResetTokenExpiredException;
import com.gramant.auth.domain.ex.PasswordResetTokenNotFoundException;
import com.gramant.auth.domain.ex.UserMissingException;
import com.gramant.auth.ports.rest.request.PasswordResetRequest;
import com.gramant.auth.ports.rest.request.PasswordUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface PasswordResetOperations {

    void requestPasswordChange(@NotNull @Valid PasswordResetRequest passwordResetToken) throws UserMissingException;

    PasswordResetToken confirmPasswordChange(PasswordResetTokenId token)
            throws PasswordResetTokenNotFoundException, PasswordResetTokenExpiredException;

    void updatePassword(@NotNull @Valid PasswordUpdateRequest passwordUpdateRequest)
            throws PasswordResetTokenExpiredException, PasswordResetTokenNotFoundException, UserMissingException;


    // default implementation
    @Service
    @AllArgsConstructor
    @Validated
    class Default implements PasswordResetOperations {

        private PasswordTokenRepository passwordTokenRepository;
        private UserRepository userRepository;
        private Notifier notifier;
        private PasswordEncoder encoder;

        @Override
        public void requestPasswordChange(@NotNull @Valid PasswordResetRequest passwordResetRequest) throws UserMissingException {
            User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                    .orElseThrow(() -> new UserMissingException(passwordResetRequest.getEmail()));
            PasswordResetToken passwordResetToken = new PasswordResetToken(user);
            passwordTokenRepository.add(passwordResetToken);
            notifier.resetPassword(passwordResetToken);
        }

        @Override
        public PasswordResetToken confirmPasswordChange(PasswordResetTokenId tokenId)
                throws PasswordResetTokenExpiredException, PasswordResetTokenNotFoundException {
            return getValidToken(tokenId);
        }

        @Override
        @Transactional
        public void updatePassword(@NotNull @Valid PasswordUpdateRequest passwordUpdateRequest) throws PasswordResetTokenExpiredException, PasswordResetTokenNotFoundException, UserMissingException {
            PasswordResetToken token = getValidToken(passwordUpdateRequest.getTokenId());
            User user = userRepository.get(token.user().id()).orElseThrow(() -> new UserMissingException(token.user().id()));
            userRepository.update(user.withPassword(encoder.encode(passwordUpdateRequest.getPassword())));
            passwordTokenRepository.remove(token.tokenId());
            notifier.resetPasswordSuccess(user);
        }

        private PasswordResetToken getValidToken(PasswordResetTokenId tokenId) throws PasswordResetTokenNotFoundException, PasswordResetTokenExpiredException {
            PasswordResetToken token = passwordTokenRepository.get(tokenId).orElseThrow(() -> new PasswordResetTokenNotFoundException(tokenId));
            if (token.expired()) {
                passwordTokenRepository.remove(tokenId);
                throw new PasswordResetTokenExpiredException(tokenId, token.expiryDate());
            }
            return token;
        }
    }

}
