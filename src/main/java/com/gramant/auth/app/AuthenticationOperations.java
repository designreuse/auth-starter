package com.gramant.auth.app;

import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.domain.MetaUser;
import com.gramant.auth.domain.User;
import org.springframework.security.core.Authentication;

import java.util.Optional;

public interface AuthenticationOperations {

    Optional<MetaUser> confirmAuthentication(Authentication authentication);


    // default implementation
    class Default implements AuthenticationOperations {

        @Override
        public Optional<MetaUser> confirmAuthentication(Authentication authentication) {
            return Optional.ofNullable(authentication).map(this::asMetaUser);
        }

        private MetaUser asMetaUser(Authentication authentication) {
            AuthenticatedUserDetails principal = (AuthenticatedUserDetails) authentication.getPrincipal();
            User user = principal.getUser();
            boolean impersonated = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PREVIOUS_ADMINISTRATOR"));

            return new MetaUser(user, principal, impersonated);
        }
    }
}
