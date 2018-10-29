package com.gramant.auth.adapters.rest.representation;

import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PrivilegedUserRepresentation {
    private String id;
    private String email;
    private List<String> privileges;

    public PrivilegedUserRepresentation(AuthenticatedUserDetails userDetails) {
        User user = userDetails.getUser();

        this.id = user.id().asString();
        this.email = user.email();
        this.privileges = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
