package com.gramant.auth.app;

import com.gramant.auth.domain.PrivilegedRole;
import com.gramant.auth.domain.RoleId;
import com.gramant.auth.domain.ex.RoleMissingException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public interface RoleProvider {
    Optional<PrivilegedRole> role(RoleId roleId);

    default List<PrivilegedRole> roles(Collection<RoleId> roleIds) {
        return roleIds.stream()
                .map(roleId -> role(roleId).<RoleMissingException>orElseThrow(() -> new RoleMissingException(roleId)))
                .collect(toList());
    }

    PrivilegedRole defaultRole();

    // default implementation
    class Default implements RoleProvider {
        private final Map<RoleId, PrivilegedRole> mappedRoles;

        public Default(PrivilegedRole... roles) {
            this.mappedRoles = Optional.ofNullable(roles).map(Stream::of).map(s -> s.collect(toMap(PrivilegedRole::id, r -> r))).orElse(emptyMap());
        }

        @Override
        public Optional<PrivilegedRole> role(RoleId roleId) {
            return Optional.ofNullable(mappedRoles.get(roleId));
        }

        @Override
        public PrivilegedRole defaultRole() {
            return PrivilegedRole.user();
        }
    }
}
