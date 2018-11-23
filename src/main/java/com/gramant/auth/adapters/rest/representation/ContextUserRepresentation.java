package com.gramant.auth.adapters.rest.representation;

import com.gramant.auth.domain.*;
import lombok.Getter;

import java.util.List;

import static java.util.stream.Collectors.toList;

// representation of current user (with dynamically granted authorities?)
@Getter
public class ContextUserRepresentation {
    private String id;
    private String email;
    private List<String> privileges;
    private Object additionalData;
    private Boolean impersonate;

    public ContextUserRepresentation(MetaUser metaUser) {
        User user = metaUser.getUserDetails().getUser();
        this.id = user.id().asString();
        this.email = user.email();
        this.privileges = user.roles().stream().flatMap(pr -> pr.privileges().stream()).map(PrivilegeId::asString).collect(toList());
        this.additionalData = metaUser.getUserDetails().getAdditionalData();
        this.impersonate = metaUser.isImpersonated();
    }
}
