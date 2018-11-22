package com.gramant.auth.domain;

import lombok.Getter;

@Getter
public class MetaUser {

    private final User user;
    private final AuthenticatedUserDetails userDetails;
    private final boolean impersonated;

    public MetaUser(User user, AuthenticatedUserDetails userDetails, boolean impersonationFlag) {
        this.user = user;
        this.userDetails = userDetails;
        this.impersonated = impersonationFlag;
    }
}
