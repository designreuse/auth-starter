package com.gramant.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class VerificationToken {

    private static final long EXPIRY_SECONDS = (10 * 60);

    private final VerificationTokenType type;
    private final VerificationTokenId tokenId;
    private final User user;
    private final LocalDateTime expiryDate;

    public VerificationToken(User user, VerificationTokenType type) {
        this.user = user;
        this.tokenId = VerificationTokenId.of(UUID.randomUUID().toString());
        this.expiryDate = LocalDateTime.now().plusSeconds(EXPIRY_SECONDS);
        this.type = type;
    }

    public boolean expired() {
        return expiryDate.compareTo(LocalDateTime.now()) < 0;
    }
}
