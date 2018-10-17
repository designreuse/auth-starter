package com.gramant.auth.ports.rest.representation;

import com.gramant.auth.domain.Role;
import com.gramant.auth.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserRepresentation {
    private String id;
    private String email;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private List<Role> roles;

    public UserRepresentation(User user) {
        this.id = user.getId().asString();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.lastLogin = user.getLastLogin();
        this.roles = user.getRoles();
    }
}
