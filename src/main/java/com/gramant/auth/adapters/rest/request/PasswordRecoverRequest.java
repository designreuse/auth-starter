package com.gramant.auth.adapters.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class PasswordRecoverRequest {

    @Email
    private String email;

    @JsonCreator
    public PasswordRecoverRequest(@JsonProperty("email") String email) {
        this.email = email;
    }
}