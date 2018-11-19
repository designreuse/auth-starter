package com.gramant.auth.domain.event;

import com.gramant.auth.domain.VerificationToken;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class EmailConfirmationRequested {
    private final String email;
    private final VerificationToken token;

    public EmailConfirmationRequested(String email, VerificationToken token) {
        this.email = email;
        this.token = token;
    }
}
