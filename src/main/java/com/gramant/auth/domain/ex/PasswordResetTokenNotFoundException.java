package com.gramant.auth.domain.ex;

import com.gramant.auth.domain.PasswordResetTokenId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PasswordResetTokenNotFoundException extends Exception {

    public PasswordResetTokenNotFoundException(PasswordResetTokenId token) {
        super("Cannot find password reset token " + token);
    }
}
