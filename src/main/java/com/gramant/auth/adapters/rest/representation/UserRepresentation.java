package com.gramant.auth.adapters.rest.representation;

import com.gramant.auth.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserRepresentation {
    private String id;
    private String email;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private List<PrivilegedRoleRepresentation> roles;

    public UserRepresentation(User user) {
        this.id = user.id().asString();
        this.email = user.email();
        this.enabled = user.enabled();
        this.lastLogin = user.lastLogin();
        this.roles = user.roles().stream().map(PrivilegedRoleRepresentation::new).collect(Collectors.toList());
    }
}
