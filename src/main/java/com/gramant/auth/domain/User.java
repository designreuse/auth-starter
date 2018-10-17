package com.gramant.auth.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

/**
 * User
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"email"})
@ToString(exclude = "password")
@Getter
public class User {
    private UserId id;
    private String email;
    private String password;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private List<Role> roles;

    @Builder
    public User(UserId id, String email, String password, boolean enabled, List<Role> roles, LocalDateTime lastLogin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.roles = Optional.ofNullable(roles).orElseGet(Collections::emptyList);
        this.lastLogin = lastLogin;
    }

    public User(String email, List<Role> roles) {
        this(null, email, null, true, roles, null);
    }

    public User(String email, String password, String id) {
        this(UserId.of(id), email, password, true, singletonList(Role.USER), null);
    }

    public User updatedWith(String email, boolean enabled, List<Role> roles) {
        return new User(this.id, email, this.password, enabled, roles, this.lastLogin);
    }

    public boolean hasId() {
        return id != null;
    }

    public boolean hasRoles() {
        return roles != null && roles.size() > 0;
    }

    public User withId(UserId userId) {
        return new User(userId, this.email, this.password, this.enabled, this.roles, this.lastLogin);
    }

    public User withPassword(String password) {
        return new User(this.id, this.email, password, this.enabled, this.roles, this.lastLogin);
    }

    public User withRoles(List<Role> roles) {
        return new User(this.id, this.email, this.password, this.enabled, roles, this.lastLogin);
    }

    public User deactivated() {
        return new User(this.id, this.email, this.password, false, this.roles, this.lastLogin);
    }

    public User activated() {
        return new User(this.id, this.email, this.password, true, this.roles, this.lastLogin);
    }
}
