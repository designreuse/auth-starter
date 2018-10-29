package com.gramant.auth.adapters.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gramant.auth.domain.PasswordResetTokenId;
import lombok.Getter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class PasswordUpdateRequest {

    @NotEmpty
    @Size(min = 8)
    private String password;

    @NotEmpty
    @Size(min = 8)
    private String confirmPassword;

    @NotNull
    private PasswordResetTokenId tokenId;

    @JsonCreator
    public PasswordUpdateRequest(@JsonProperty("password") String password,
                                 @JsonProperty("confirmPassword") String confirmPassword,
                                 @JsonProperty("token") PasswordResetTokenId tokenId) {
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.tokenId = tokenId;
    }

    @AssertTrue
    public boolean isPasswordEqual() {
        return password.equals(confirmPassword);
    }
}
