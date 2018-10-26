package com.gramant.auth;

import com.gramant.auth.app.ManageUser;
import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.domain.User;
import com.gramant.auth.domain.ex.UserMissingException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

    private ManageUser userManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;

        try {
            user = userManager.findEnabledByEmail(username);
        } catch (UserMissingException e) {
            throw new UsernameNotFoundException("User " + username + " is not found");
        }

        return new AuthenticatedUserDetails(user);
    }
}
