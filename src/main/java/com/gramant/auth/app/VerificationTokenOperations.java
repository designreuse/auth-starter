package com.gramant.auth.app;

import com.gramant.auth.adapters.rest.request.PasswordRecoverRequest;
import com.gramant.auth.adapters.rest.request.PasswordUpdateRequest;
import com.gramant.auth.domain.*;
import com.gramant.auth.domain.event.EmailConfirmationCompleted;
import com.gramant.auth.domain.event.EmailConfirmationRequested;
import com.gramant.auth.domain.event.PasswordChangeCompleted;
import com.gramant.auth.domain.event.PasswordChangeRequested;
import com.gramant.auth.domain.ex.UserMissingException;
import com.gramant.auth.domain.ex.VerificationTokenExpiredException;
import com.gramant.auth.domain.ex.VerificationTokenNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface VerificationTokenOperations {

    // todo: переименовать в requestPasswordChange; также и request
    void requestPasswordRecover(@NotNull @Valid PasswordRecoverRequest passwordRecoverRequest) throws UserMissingException;

    VerificationToken getValidToken(VerificationTokenId id) throws VerificationTokenNotFoundException, VerificationTokenExpiredException;

    default VerificationToken confirmPasswordChange(VerificationTokenId id) throws VerificationTokenNotFoundException, VerificationTokenExpiredException {
        return getValidToken(id);
    }

    void updatePassword(@NotNull @Valid PasswordUpdateRequest passwordUpdateRequest) throws VerificationTokenExpiredException, VerificationTokenNotFoundException, UserMissingException;

    void requestEmailConfirmation(@NotNull User user);

    void confirmEmail(VerificationTokenId id) throws VerificationTokenExpiredException, VerificationTokenNotFoundException, UserMissingException;


    @AllArgsConstructor
    @Validated
    class Default implements VerificationTokenOperations {

        private VerificationTokenRepository verificationTokenRepository;
        private UserRepository userRepository;
        private PasswordEncoder encoder;
        private ApplicationEventPublisher eventPublisher;

        @Override
        public void requestPasswordRecover(@NotNull @Valid PasswordRecoverRequest passwordRecoverRequest) throws UserMissingException {
            User user = userRepository.findByEmail(passwordRecoverRequest.getEmail())
                    .orElseThrow(() -> new UserMissingException(passwordRecoverRequest.getEmail()));
            VerificationToken verificationToken = new VerificationToken(user.id(), VerificationTokenType.PASSWORD);

            Optional<VerificationToken> oldToken = verificationTokenRepository.findByUserId(user.id());
            oldToken.ifPresent(old -> verificationTokenRepository.remove(old.tokenId()));

            verificationTokenRepository.add(verificationToken);
            eventPublisher.publishEvent(new PasswordChangeRequested(passwordRecoverRequest.getEmail(), verificationToken));
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
            User user = userRepository.get(token.userId()).orElseThrow(() -> new UserMissingException(token.userId()));
            userRepository.update(user.withPassword(encoder.encode(passwordUpdateRequest.getPassword())));
            verificationTokenRepository.remove(token.tokenId());

            eventPublisher.publishEvent(new PasswordChangeCompleted(user));
        }

        @Override
        public void requestEmailConfirmation(@NotNull User user) {
            VerificationToken token = new VerificationToken(user.id(), VerificationTokenType.EMAIL);
            verificationTokenRepository.add(token);

            eventPublisher.publishEvent(new EmailConfirmationRequested(user.email(), token));
        }

        @Override
        public void confirmEmail(VerificationTokenId id) throws VerificationTokenNotFoundException, VerificationTokenExpiredException, UserMissingException {
            VerificationToken token = getValidToken(id);
            User user = userRepository.get(token.userId()).orElseThrow(() -> new UserMissingException(token.userId()));
            userRepository.update(user.asEnabled());
            verificationTokenRepository.remove(id);

            eventPublisher.publishEvent(new EmailConfirmationCompleted(user));
        }
    }
}
