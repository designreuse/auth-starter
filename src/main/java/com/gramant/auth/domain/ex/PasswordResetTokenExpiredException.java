package com.gramant.auth.domain.ex;

import com.gramant.auth.domain.PasswordResetTokenId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(HttpStatus.GONE)
public class PasswordResetTokenExpiredException extends Exception {

    public PasswordResetTokenExpiredException(PasswordResetTokenId token, LocalDateTime expiryDate) {
        super("Password reset token " + token + " expired at " + expiryDate);
    }
}
