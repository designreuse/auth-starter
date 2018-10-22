package com.gramant.auth.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

import static java.util.Collections.*;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "privileges"})
public final class PrivilegedRole {

    private final RoleId id;
    private final List<PrivilegeId> privileges;

    public static PrivilegedRole admin() {
        return new PrivilegedRole(RoleId.ADMIN, singletonList(PrivilegeId.ALL));
    }

    public static PrivilegedRole unknown() {
        return new PrivilegedRole(RoleId.UNKNOWN, emptyList());
    }

    public static PrivilegedRole user() {
        return new PrivilegedRole(RoleId.USER, emptyList());
    }
}

