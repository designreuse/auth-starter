package com.gramant.auth.domain.event;

import com.gramant.auth.domain.PrivilegedRole;
import com.gramant.auth.domain.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class UserCreatedEvent {
    private final UserId id;
    private final String email;
    private final List<PrivilegedRole> roles;
    private final Map<String, String> additionalProperties;
}
