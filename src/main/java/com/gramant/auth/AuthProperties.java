package com.gramant.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth-starter")
@Getter
@Setter
public class AuthProperties {
    private String username;
    private Boolean enablePersistentLogins = false;
    private Integer rememberMeTokenValiditySeconds = 86400;
    private String remeberMeKey;
    private Boolean confirmEmail = false;
}