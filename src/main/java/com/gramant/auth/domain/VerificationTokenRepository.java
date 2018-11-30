package com.gramant.auth.domain;

import java.util.Optional;

public interface VerificationTokenRepository {

    void add(VerificationToken token);

    Optional<VerificationToken> get(VerificationTokenId tokenId);

    Optional<VerificationToken> findByUserId(UserId id);

    void remove(VerificationTokenId tokenId);
}
