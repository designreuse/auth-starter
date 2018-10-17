package com.gramant.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("auth-starter")
public class AuthProperties {
    String username;


}