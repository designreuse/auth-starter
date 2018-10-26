package com.gramant.auth;

import com.gramant.auth.app.ManageUser;
import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.domain.User;
import com.gramant.auth.domain.ex.UserMissingException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserDetailsPreloader {

    @Bean
    public UserDetailsService userDetailsService(ManageUser manageUser) {
        return username -> {
            User user;

            try {
                user = manageUser.findEnabledByEmail(username);
            } catch (UserMissingException e) {
                throw new UsernameNotFoundException("User " + username + " is not found");
            }

            return new AuthenticatedUserDetails(user);
        };
    }
}
