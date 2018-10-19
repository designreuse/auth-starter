package com.gramant.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(of = "value")
@ToString(of = "value")
public final class PrivilegeId implements Serializable {

    public static final PrivilegeId ALL = new PrivilegeId("ALL");

    private final String value;

    public PrivilegeId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        this.value = value;
    }

    public String asString() {
        return value;
    }
}
