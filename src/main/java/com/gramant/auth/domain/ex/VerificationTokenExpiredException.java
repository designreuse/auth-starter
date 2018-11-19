package com.gramant.auth.domain.ex;

import com.gramant.auth.domain.VerificationTokenId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(HttpStatus.GONE)
public class VerificationTokenExpiredException extends Exception {

    public VerificationTokenExpiredException(VerificationTokenId id, LocalDateTime expiryDate) {
        super("Verification token " + id + " expired at " + expiryDate);
    }
}
