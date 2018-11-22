package com.gramant.auth.adapters.rest.representation;

import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.domain.User;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PrivilegedUserRepresentation {
    private String id;
    private String email;
    private List<String> privileges;
    private Object additionalData;
    private Boolean impersonate;

    // fixme: убрать все знание о org.springframework.security
    public PrivilegedUserRepresentation(Authentication authentication) {
        AuthenticatedUserDetails principal = (AuthenticatedUserDetails) authentication.getPrincipal();
        User user = principal.getUser();

        this.id = user.id().asString();
        this.email = user.email();
        this.privileges = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        this.additionalData = principal.getAdditionalData();
        this.impersonate = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PREVIOUS_ADMINISTRATOR"));
    }
}
