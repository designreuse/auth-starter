package com.gramant.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(of = "value")
@ToString
public class UserId {

    private String value;

    private UserId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        this.value = value;
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public String asString() {
        return value;
    }
}
