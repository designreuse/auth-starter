package com.gramant.auth.app;

import com.gramant.auth.adapters.rest.request.PasswordResetRequest;
import com.gramant.auth.adapters.rest.request.PasswordUpdateRequest;
import com.gramant.auth.domain.*;
import com.gramant.auth.domain.event.EmailConfirmationCompleted;
import com.gramant.auth.domain.event.EmailConfirmationRequested;
import com.gramant.auth.domain.event.PasswordResetCompleted;
import com.gramant.auth.domain.event.PasswordResetRequested;
import com.gramant.auth.domain.ex.UserMissingException;
import com.gramant.auth.domain.ex.VerificationTokenExpiredException;
import com.gramant.auth.domain.ex.VerificationTokenNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface VerificationTokenOperations {

    void requestPasswordChange(@NotNull @Valid PasswordResetRequest passwordResetRequest) throws UserMissingException;

    VerificationToken getValidToken(VerificationTokenId id) throws VerificationTokenNotFoundException, VerificationTokenExpiredException;

    default VerificationToken confirmPasswordChange(VerificationTokenId id) throws VerificationTokenNotFoundException, VerificationTokenExpiredException {
        return getValidToken(id);
    }

    void updatePassword(@NotNull @Valid PasswordUpdateRequest passwordUpdateRequest) throws VerificationTokenExpiredException, VerificationTokenNotFoundException, UserMissingException;

    void requestEmailConfirmation(@NotNull String email) throws UserMissingException;

    void confirmEmail(VerificationTokenId id) throws VerificationTokenExpiredException, VerificationTokenNotFoundException, UserMissingException;


    @AllArgsConstructor
    @Validated
    class Default implements VerificationTokenOperations {

        private VerificationTokenRepository verificationTokenRepository;
        private UserRepository userRepository;
        private PasswordEncoder encoder;
        private ApplicationEventPublisher eventPublisher;

        @Override
        public void requestPasswordChange(@NotNull @Valid PasswordResetRequest passwordResetRequest) throws UserMissingException {
            User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                    .orElseThrow(() -> new UserMissingException(passwordResetRequest.getEmail()));
            VerificationToken verificationToken = new VerificationToken(user, VerificationTokenType.PASSWORD);
            verificationTokenRepository.add(verificationToken);

            eventPublisher.publishEvent(new PasswordResetRequested(passwordResetRequest.getEmail(), verificationToken));
        }

        @Override
        public VerificationToken getValidToken(VerificationTokenId id) throws VerificationTokenNotFoundException, VerificationTokenExpiredException {
            VerificationToken token = verificationTokenRepository.get(id).orElseThrow(() -> new VerificationTokenNotFoundException(id));
            if (token.expired()) {
                verificationTokenRepository.remove(id);
                throw new VerificationTokenExpiredException(id, token.expiryDate());
            }
            return token;
        }

        @Override
        public void updatePassword(@NotNull @Valid PasswordUpdateRequest passwordUpdateRequest) throws VerificationTokenExpiredException, VerificationTokenNotFoundException, UserMissingException {
            VerificationToken token = getValidToken(passwordUpdateRequest.getTokenId());
            User user = userRepository.get(token.user().id()).orElseThrow(() -> new UserMissingException(token.user().id()));
            userRepository.update(user.withPassword(encoder.encode(passwordUpdateRequest.getPassword())));
            verificationTokenRepository.remove(token.tokenId());

            eventPublisher.publishEvent(new PasswordResetCompleted(user));
        }

        @Override
        public void requestEmailConfirmation(@NotNull String email) throws UserMissingException {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UserMissingException(email));
            VerificationToken token = new VerificationToken(user, VerificationTokenType.EMAIL);
            verificationTokenRepository.add(token);

            eventPublisher.publishEvent(new EmailConfirmationRequested(email, token));
        }

        @Override
        public void confirmEmail(VerificationTokenId id) throws VerificationTokenNotFoundException, VerificationTokenExpiredException, UserMissingException {
            VerificationToken token = getValidToken(id);
            User user = userRepository.get(token.user().id()).orElseThrow(() -> new UserMissingException(token.user().id()));
            userRepository.update(user.asActivated());
            verificationTokenRepository.remove(id);

            eventPublisher.publishEvent(new EmailConfirmationCompleted(user));
        }
    }
}
