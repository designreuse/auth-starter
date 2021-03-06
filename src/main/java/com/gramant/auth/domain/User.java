package com.gramant.auth.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * User
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = "password")
public class User {
    private UserId id;
    private String email;
    private String password;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private boolean nonLocked;
    private final List<PrivilegedRole> roles;

    @Builder
    public User(UserId id, String email, String password, boolean enabled, boolean nonLocked, List<PrivilegedRole> roles, LocalDateTime lastLogin) {
        Objects.requireNonNull(id);
        this.id = id;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.nonLocked = nonLocked;
        this.roles = Optional.ofNullable(roles).orElseGet(Collections::emptyList);
        this.lastLogin = lastLogin;
    }

    public User updatedWith(String email, boolean nonLocked, List<PrivilegedRole> roles) {
        return new User(this.id, email, this.password, this.enabled, nonLocked, roles, this.lastLogin);
    }

    public User withId(UserId userId) {
        return new User(userId, this.email, this.password, this.enabled, this.nonLocked, this.roles, this.lastLogin);
    }

    public User withPassword(String password) {
        return new User(this.id, this.email, password, this.enabled, this.nonLocked, this.roles, this.lastLogin);
    }

    public User withRoles(List<PrivilegedRole> roles) {
        return new User(this.id, this.email, this.password, this.enabled, this.nonLocked, roles, this.lastLogin);
    }

    public User asLocked() {
        return new User(this.id, this.email, this.password, this.enabled, false, this.roles, this.lastLogin);
    }

    public User asNonLocked() {
        return new User(this.id, this.email, this.password, this.enabled, true, this.roles, this.lastLogin);
    }

    public User asEnabled() {
        return new User(this.id, this.email, this.password, true, this.nonLocked, this.roles, this.lastLogin);
    }
}
