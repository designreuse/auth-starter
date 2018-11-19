package com.gramant.auth.domain;

import java.util.Optional;

public interface VerificationTokenRepository {

    void add(VerificationToken token);

    Optional<VerificationToken> get(VerificationTokenId tokenId);

    void remove(VerificationTokenId tokenId);
}
