package com.gramant.auth.adapters.rest.representation;

import com.gramant.auth.domain.PrivilegeId;
import com.gramant.auth.domain.PrivilegedRole;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class PrivilegedRoleRepresentation {

    private String roleId;
    private Collection<String> privileges;

    public PrivilegedRoleRepresentation(PrivilegedRole privilegedRole) {
        this.roleId = privilegedRole.id().asString();
        this.privileges = privilegedRole.privileges().stream().map(PrivilegeId::asString).collect(Collectors.toList());
    }
}
