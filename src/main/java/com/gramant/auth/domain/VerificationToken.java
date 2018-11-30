package com.gramant.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class VerificationToken {

    private static final long EXPIRY_SECONDS = (10 * 60);

    private final VerificationTokenType type;
    private final VerificationTokenId tokenId;
    private final UserId userId;
    private final LocalDateTime expiryDate;

    public VerificationToken(UserId userId, VerificationTokenType tokenType) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(tokenType);
        this.userId = userId;
        this.tokenId = VerificationTokenId.of(UUID.randomUUID().toString());
        this.expiryDate = LocalDateTime.now().plusSeconds(EXPIRY_SECONDS * tokenType.getTimeFactor());
        this.type = tokenType;
    }

    public boolean expired() {
        return expiryDate.compareTo(LocalDateTime.now()) < 0;
    }
}
