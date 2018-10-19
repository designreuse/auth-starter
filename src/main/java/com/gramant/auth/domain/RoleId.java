package com.gramant.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(of = "value")
@ToString(of = "value")
public final class RoleId implements Serializable {

    public static final RoleId ADMIN = new RoleId("ADMIN");
    public static final RoleId UNKNOWN = new RoleId("UNKNOWN");
    public static final RoleId USER = new RoleId("USER");

    private final String value;

    public RoleId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value == null");
        }
        this.value = value;
    }

    public String asString() {
        return value;
    }
}
