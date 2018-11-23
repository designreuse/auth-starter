package com.gramant.auth.domain;

import lombok.Getter;

@Getter
public class MetaUser {

    private final AuthenticatedUserDetails userDetails;
    private final boolean impersonated;

    public MetaUser(AuthenticatedUserDetails userDetails, boolean impersonationFlag) {
        this.userDetails = userDetails;
        this.impersonated = impersonationFlag;
    }
}
