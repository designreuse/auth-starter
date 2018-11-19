package com.gramant.auth.domain.ex;

import com.gramant.auth.domain.VerificationTokenId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VerificationTokenNotFoundException extends Exception {

    public VerificationTokenNotFoundException(VerificationTokenId id) {
        super("Cannot find verification token " + id);
    }
}
