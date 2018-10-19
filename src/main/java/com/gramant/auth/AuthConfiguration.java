package com.gramant.auth;

import com.gramant.auth.app.DefaultUserManager;
import com.gramant.auth.app.ManageUser;
import com.gramant.auth.app.Notifier;
import com.gramant.auth.app.PasswordResetOperations;
import com.gramant.auth.domain.PasswordResetToken;
import com.gramant.auth.domain.PasswordTokenRepository;
import com.gramant.auth.domain.User;
import com.gramant.auth.domain.UserRepository;
import com.gramant.auth.ports.jdbc.JdbcPasswordTokenRepository;
import com.gramant.auth.ports.jdbc.JdbcUserRepository;
import com.gramant.auth.ports.rest.ProfileResource;
import com.gramant.auth.ports.rest.ExistsValidationResource;
import com.gramant.auth.ports.rest.UserResource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass(UserDetailsService.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
@EnableConfigurationProperties(AuthProperties.class)
@Import(WebSecurityConfig.class)
public class AuthConfiguration {
    @Bean
    public AuthListener authListener() {
        return new AuthListener();
    }

    @Bean
    public UserResource userResource(ManageUser manageUser, PasswordResetOperations passwordResetOperations) {
        return new UserResource(manageUser, passwordResetOperations);
    }

    @Bean
    public ProfileResource authResource(ManageUser manageUser) {
        return new ProfileResource();
    }

    @Bean
    public ExistsValidationResource existsValidationResource(ManageUser manageUser) {
        return new ExistsValidationResource(manageUser);
    }

    @Bean
    public PasswordResetOperations passwordResetOperations(
            PasswordTokenRepository passwordTokenRepository,
            UserRepository userRepository,
            Notifier notifier,
            PasswordEncoder passwordEncoder) {
        return new PasswordResetOperations.Default(passwordTokenRepository, userRepository, notifier, passwordEncoder);
    }

    @Bean
    public PasswordTokenRepository passwordTokenRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcPasswordTokenRepository(jdbcTemplate);
    }

    @Bean
    public UserRepository userRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcUserRepository(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Bean
    public Notifier notifier() {
        return new Notifier() {
            @Override
            public void registrationSuccess(User createdUser) {
                throw new UnsupportedOperationException("not implemented!");
            }

            @Override
            public void communicate(User user, String message) {
                throw new UnsupportedOperationException("not implemented!");
            }

            @Override
            public void resetPassword(PasswordResetToken token) {
                throw new UnsupportedOperationException("not implemented!");
            }

            @Override
            public void resetPasswordSuccess(User user) {
                throw new UnsupportedOperationException("not implemented!");
            }
        };
    }

    @Bean
    public ManageUser manageUser(UserRepository userRepository, PasswordEncoder passwordEncoder, Notifier notifier) {
        return new DefaultUserManager(userRepository, passwordEncoder, notifier);
    }
}