package com.gramant.auth;

import com.gramant.auth.app.*;
import com.gramant.auth.domain.*;
import com.gramant.auth.adapters.jdbc.JdbcVerificationTokenRepository;
import com.gramant.auth.adapters.jdbc.JdbcUserRepository;
import com.gramant.auth.adapters.rest.ProfileResource;
import com.gramant.auth.adapters.rest.ExistsValidationResource;
import com.gramant.auth.adapters.rest.UserResource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
@EnableConfigurationProperties(AuthProperties.class)
@Import(WebSecurityConfig.class)
public class AuthConfiguration {

    @Bean
    public AuthListener authListener() {
        return new AuthListener();
    }

    @Bean
    public UserResource userResource(ManageUser manageUser, VerificationTokenOperations verificationTokenOperations, PreProcessRegistrationStep step) {
        return new UserResource(manageUser, verificationTokenOperations, step);
    }

    @Bean
    public ProfileResource authResource() {
        return new ProfileResource();
    }

    @Bean
    public ExistsValidationResource existsValidationResource(QueryUser queryUser) {
        return new ExistsValidationResource(queryUser);
    }

    @Bean
    public VerificationTokenOperations verificationTokenOperations(
            VerificationTokenRepository verificationTokenRepository,
            UserRepository userRepository,
            Notifier notifier,
            PasswordEncoder passwordEncoder) {
        return new VerificationTokenOperations.Default(verificationTokenRepository, userRepository, notifier, passwordEncoder);
    }

    @Bean
    public VerificationTokenRepository passwordTokenRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcVerificationTokenRepository(jdbcTemplate);
    }

    @Bean
    public UserRepository userRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, RoleProvider roleProvider) {
        return new JdbcUserRepository(jdbcTemplate, namedParameterJdbcTemplate, roleProvider);
    }

    @Bean
    @ConditionalOnMissingBean
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
            public void resetPassword(VerificationToken token) {
                throw new UnsupportedOperationException("not implemented!");
            }

            @Override
            public void resetPasswordSuccess(User user) {
                throw new UnsupportedOperationException("not implemented!");
            }

            @Override
            public void confirmEmail(VerificationToken token) {
                throw new UnsupportedOperationException("not implemented!");
            }

            @Override
            public void confirmEmailSuccess(User user) {
                throw new UnsupportedOperationException("not implemented!");
            }
        };
    }

    @Bean
    public ManageUser manageUser(UserRepository userRepository, Notifier notifier, RoleProvider roleProvider,
                                 ApplicationEventPublisher eventPublisher, AuthProperties authProperties,
                                 VerificationTokenOperations verificationTokenOperations) {
        return new DefaultUserManager(userRepository, passwordEncoder(), notifier, roleProvider, eventPublisher,
                authProperties, verificationTokenOperations);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public QueryUser queryUser(UserRepository userRepository) {
        return new QueryUser.Default(userRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    public PreProcessRegistrationStep preProcessRegistrationStep() {
        return (request) -> request;
    }

    @Bean
    @ConditionalOnMissingBean
    public RoleProvider roleProvider() {
        return new RoleProvider.Default(PrivilegedRole.admin(), PrivilegedRole.user());
    }

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }
}