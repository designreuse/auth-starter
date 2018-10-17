package com.gramant.auth.domain;

import java.util.Optional;

public interface PasswordTokenRepository {

    void add(PasswordResetToken passwordResetToken);

    Optional<PasswordResetToken> get(PasswordResetTokenId tokenId);

    void remove(PasswordResetTokenId tokenId);
}
