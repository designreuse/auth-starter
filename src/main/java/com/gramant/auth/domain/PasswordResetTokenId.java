package com.gramant.auth.domain;

import lombok.ToString;

@ToString(of = {"value"})
public class PasswordResetTokenId {

    private String value;

    private PasswordResetTokenId(String value) {
        this.value = value;
    }

    public static PasswordResetTokenId of(String value) {
        return new PasswordResetTokenId(value);
    }

    public String asString() {
        return this.value;
    }
}
