package com.gramant.auth.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * User
 */
@Getter
//@Accessors(fluent = true)
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = "password")
public class User {
    private UserId id;
    private String email;
    private String password;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private final List<PrivilegedRole> roles;

    @Builder
    public User(UserId id, String email, String password, boolean enabled, List<PrivilegedRole> roles, LocalDateTime lastLogin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.roles = Optional.ofNullable(roles).orElseGet(Collections::emptyList);
        this.lastLogin = lastLogin;
    }

    public User updatedWith(String email, boolean enabled, List<PrivilegedRole> roles) {
        return new User(this.id, email, this.password, enabled, roles, this.lastLogin);
    }

    public User withId(UserId userId) {
        return new User(userId, this.email, this.password, this.enabled, this.roles, this.lastLogin);
    }

    public User withPassword(String password) {
        return new User(this.id, this.email, password, this.enabled, this.roles, this.lastLogin);
    }

    public User withRoles(List<PrivilegedRole> roles) {
        return new User(this.id, this.email, this.password, this.enabled, roles, this.lastLogin);
    }

    public User asDeactivated() {
        return new User(this.id, this.email, this.password, false, this.roles, this.lastLogin);
    }

    public User asActivated() {
        return new User(this.id, this.email, this.password, true, this.roles, this.lastLogin);
    }
}
