package com.gramant.auth.app;

import com.gramant.auth.domain.PrivilegedRole;
import com.gramant.auth.domain.RoleId;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

public interface RoleProvider {
    Optional<PrivilegedRole> role(RoleId roleId);

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
