package com.gramant.auth;

import com.gramant.auth.app.*;
import com.gramant.auth.domain.*;
import com.gramant.auth.adapters.jdbc.JdbcVerificationTokenRepository;
import com.gramant.auth.adapters.jdbc.JdbcUserRepository;
import com.gramant.auth.adapters.rest.ProfileResource;
import com.gramant.auth.adapters.rest.ExistsValidationResource;
import com.gramant.auth.adapters.rest.UserResource;
import com.gramant.auth.domain.event.*;
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
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
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
    public ProfileResource authResource(AuthenticationOperations authenticationOperations) {
        return new ProfileResource(authenticationOperations);
    }

    @Bean
    public ExistsValidationResource existsValidationResource(QueryUser queryUser) {
        return new ExistsValidationResource(queryUser);
    }

    @Bean
    public AuthenticationOperations authenticationOperations() {
        return new AuthenticationOperations.Default();
    }

    @Bean
    public VerificationTokenOperations verificationTokenOperations(
            VerificationTokenRepository verificationTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ApplicationEventPublisher eventPublisher) {
        return new VerificationTokenOperations.Default(verificationTokenRepository, userRepository, passwordEncoder, eventPublisher);
    }

    @Bean
    public VerificationTokenRepository passwordTokenRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcVerificationTokenRepository(jdbcTemplate);
    }

    @Bean
    public UserRepository userRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate, RoleProvider roleProvider) {
        return new JdbcUserRepository(jdbcTemplate, namedParameterJdbcTemplate, roleProvider);
    }

    @Bean UserInvariants userInvariants(UserRepository userRepository) {
        return new UserInvariants.Default(userRepository);
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
            public void recoverPassword(VerificationToken token) {
                throw new UnsupportedOperationException("not implemented!");
            }

            @Override
            public void recoverPasswordSuccess(User user) {
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

            @Override
            public void resetPasswordSuccess(User user, String newPassword) {
                throw new UnsupportedOperationException("not implemented!");
            }
        };
    }

    @Bean
    public ManageUser manageUser(UserRepository userRepository, Notifier notifier, RoleProvider roleProvider,
                                 ApplicationEventPublisher eventPublisher, AuthProperties authProperties,
                                 VerificationTokenOperations verificationTokenOperations, UserInvariants userInvariants) {
        return new DefaultUserManager(userRepository, passwordEncoder(), roleProvider, eventPublisher,
                authProperties, verificationTokenOperations, userInvariants, passwordGenerator());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public QueryUser queryUser(UserRepository userRepository, UserInvariants userInvariants) {
        return new QueryUser.Default(userRepository, userInvariants);
    }

    @Bean
    public PasswordGenerator passwordGenerator() {
        return new PasswordGenerator();
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

    @Bean
    public NotificationEventListener notificationEventListener(Notifier notifier) {
        return new NotificationEventListener(notifier);
    }

    static class NotificationEventListener {

        private final Notifier notifier;

        NotificationEventListener(Notifier notifier) {
            this.notifier = notifier;
        }

        @EventListener
        @Async
        public void processPasswordRecoverRequestedEvent(PasswordRecoverRequested event) {
            notifier.recoverPassword(event.verificationToken());
        }

        @EventListener
        @Async
        public void processPasswordRecoverCompletedEvent(PasswordRecoverCompleted event) {
            notifier.recoverPasswordSuccess(event.user());
        }

        @EventListener
        @Async
        public void processEmailConfirmationRequestedEvent(EmailConfirmationRequested event) {
            notifier.confirmEmail(event.token());
        }

        @EventListener
        @Async
        public void processEmailConfirmationCompletedEvent(EmailConfirmationCompleted event) {
            notifier.confirmEmailSuccess(event.user());
        }

        @EventListener
        @Async
        public void processPasswordResetCompletedEvent(PasswordResetCompleted event) {
            notifier.resetPasswordSuccess(event.user(), event.newPassword());
        }

        @EventListener
        @Async
        public void processUsersMessagedEvent(UsersMessaged event) {
            event.users().forEach(u -> notifier.communicate(u, event.message()));
        }
    }
}