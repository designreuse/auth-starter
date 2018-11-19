package com.gramant.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(of = "value")
@ToString(of = {"value"})
public class VerificationTokenId implements Serializable {

    private final String value;

    private VerificationTokenId(String value) {
        this.value = value;
    }

    public static VerificationTokenId of(String value) {
        return new VerificationTokenId(value);
    }

    public String asString() {
        return this.value;
    }
}
