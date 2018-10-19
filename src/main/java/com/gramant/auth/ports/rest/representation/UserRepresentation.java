package com.gramant.auth.ports.rest.representation;

import com.gramant.auth.domain.PrivilegedRole;
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
    private List<PrivilegedRole> roles;

    public UserRepresentation(User user) {
        this.id = user.id().asString();
        this.email = user.email();
        this.enabled = user.enabled();
        this.lastLogin = user.lastLogin();
        this.roles = user.roles();
    }
}
