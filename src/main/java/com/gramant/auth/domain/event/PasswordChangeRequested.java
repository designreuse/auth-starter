package com.gramant.auth.domain.event;

import com.gramant.auth.domain.VerificationToken;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class PasswordChangeRequested {
    private final String email;
    private final VerificationToken verificationToken;

    public PasswordChangeRequested(String email, VerificationToken verificationToken) {
        this.email = email;
        this.verificationToken = verificationToken;
    }
}
