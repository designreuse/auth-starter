package com.gramant.auth.test;

import com.gramant.auth.domain.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUser.WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String id() default "testCustomer";
    String email() default "test@mail.ru";
    String password() default "password";

    class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
        @Override
        public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            User user = new User(UserId.of(customUser.id()), customUser.email(), customUser.password(), true, Collections.singletonList(PrivilegedRole.user()), null);
            AuthenticatedUserDetails principal = new AuthenticatedUserDetails(user);
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
            context.setAuthentication(auth);
            return context;
        }
    }
}