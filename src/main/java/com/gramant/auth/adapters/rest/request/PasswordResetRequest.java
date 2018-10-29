package com.gramant.auth.adapters.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class PasswordResetRequest {

    @Email
    private String email;

    @JsonCreator
    public PasswordResetRequest(@JsonProperty("email") String email) {
        this.email = email;
    }
}