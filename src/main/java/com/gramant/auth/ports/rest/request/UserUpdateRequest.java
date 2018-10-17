package com.gramant.auth.ports.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gramant.auth.domain.Role;
import com.gramant.auth.domain.UserId;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class UserUpdateRequest {

    @NotNull
    private UserId id;

    @NotNull
    private Boolean enabled;

    @NotEmpty
    private String email;

    @NotEmpty
    private List<Role> roles;

    @JsonCreator
    public UserUpdateRequest(@JsonProperty("id") UserId id,
                             @JsonProperty("enabled") Boolean enabled,
                             @JsonProperty("email") String email,
                             @JsonProperty("roles") List<Integer> roles) {
        this.id = id;
        this.enabled = enabled;
        this.email = email;
        this.roles = roles.stream().map(Role::getById).collect(toList());
    }

}