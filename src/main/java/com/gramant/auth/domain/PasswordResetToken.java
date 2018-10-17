package com.gramant.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PasswordResetToken {

    private static final long EXPIRY_SECONDS = (10 * 60);
    private final PasswordResetTokenId tokenId;
    private final User user;
    private final LocalDateTime expiryDate;

    public PasswordResetToken(User user) {
        this.user = user;
        this.tokenId = PasswordResetTokenId.of(UUID.randomUUID().toString());
        this.expiryDate = LocalDateTime.now().plusSeconds(EXPIRY_SECONDS);
    }

    public boolean isExpired() {
        return expiryDate.compareTo(LocalDateTime.now()) < 0;
    }
}
