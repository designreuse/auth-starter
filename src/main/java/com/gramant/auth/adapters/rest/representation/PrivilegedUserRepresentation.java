package com.gramant.auth.adapters.rest.representation;

import com.gramant.auth.domain.*;
import lombok.Getter;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class PrivilegedUserRepresentation {
    private String id;
    private String email;
    private List<String> privileges;
    private Object additionalData;
    private Boolean impersonate;

    public PrivilegedUserRepresentation(MetaUser metaUser) {
        User user = metaUser.getUser();
        this.id = user.id().asString();
        this.email = user.email();
        this.privileges = user.roles().stream().map(PrivilegedRole::id).map(RoleId::asString).collect(toList());
        this.additionalData = metaUser.getUserDetails().getAdditionalData();
        this.impersonate = metaUser.isImpersonated();
    }
}
