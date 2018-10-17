package com.gramant.auth;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(UserDetailsService.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfiguration {
    @Bean
    public AuthListener authListener() {
        return new AuthListener();
    }
}