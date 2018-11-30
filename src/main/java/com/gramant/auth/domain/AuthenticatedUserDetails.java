package com.gramant.auth.domain;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@ToString(of = {"user"})
public class AuthenticatedUserDetails implements UserDetails {
    private final User user;
    private final Object additionalData;

    public AuthenticatedUserDetails(User user, Object additionalData) {
        this.user = user;
        this.additionalData = additionalData;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.roles().stream()
                .flatMap(r -> r.privileges().stream().map(PrivilegeId::asString)).map(SimpleGrantedAuthority::new)
                .collect(toList());
    }

    @Override
    public String getPassword() {
        return user.password();
    }

    @Override
    public String getUsername() {
        return user.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.nonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.enabled();
    }

    public User getUser() {
        return user;
    }

    public Object getAdditionalData() {
        return additionalData;
    }
}
