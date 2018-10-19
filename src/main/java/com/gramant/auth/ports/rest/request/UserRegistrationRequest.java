package com.gramant.auth.ports.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gramant.auth.domain.PrivilegedRole;
import com.gramant.auth.domain.User;
import com.gramant.auth.domain.UserId;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.function.Function;

import static java.util.Collections.singletonList;

@Getter
public class UserRegistrationRequest {

    @NotEmpty
    private String email;

    @NotEmpty
    private String id;

    @NotEmpty
    @Size(min = 8)
    private String password;

    @JsonCreator
    public UserRegistrationRequest(@JsonProperty("email") String email,
                                   @JsonProperty("id") String id,
                                   @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
        this.id = id;
    }

    public User asUserWithMappedPassword(Function<String, String> passwordMapper, PrivilegedRole defaultRole) {
        return User.builder().email(email).password(passwordMapper.apply(password)).id(UserId.of(id))
                .roles(singletonList(defaultRole)).build();
    }
}
