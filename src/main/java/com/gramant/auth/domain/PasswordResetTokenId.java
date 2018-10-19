package com.gramant.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(of = "value")
@ToString(of = {"value"})
public class PasswordResetTokenId implements Serializable {

    private final String value;

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
