package com.gramant.auth;

import com.gramant.auth.app.ManageUser;
import com.gramant.auth.domain.AuthenticatedUserDetails;
import com.gramant.auth.domain.User;
import com.gramant.auth.domain.ex.UserMissingException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.Cookie;
import java.util.Arrays;

import static java.util.Collections.singletonList;

/**
 * Spring Security Config
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static String[] allowedOrigins = new String[] {"http://localhost:80"};
    private static int rememberMeTokenValiditySeconds = 24 * 60 * 60;

    @Override
    // todo: "/api/auth/**" -> "/auth/**"
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().configurationSource(corsConfigurationSource());
        http.authorizeRequests().antMatchers(
                "/api/auth/login",
                "/api/auth/user",
                "/api/users"
        ).permitAll();
        http.authorizeRequests().antMatchers("/api/**").authenticated();
        http.logout()
                .permitAll().logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
        http.rememberMe()
                .userDetailsService(userDetailsService())
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(rememberMeTokenValiditySeconds);
        http.formLogin()
                .loginProcessingUrl("/api/auth/login")
                .successHandler(successLoginHandler())
                .failureHandler(failureLoginHandler());
        http.sessionManagement().invalidSessionStrategy((request, response) -> {
            if (!response.isCommitted()){
                Cookie cookie = new Cookie("JSESSIONID", "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            }
        });
        http.addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring().antMatchers("/api/auth/check/**")
                .and()
                .ignoring().antMatchers(HttpMethod.POST, "/api/auth/user", "/api/users/password-reset-token")
                .and()
                .ignoring().antMatchers(HttpMethod.GET, "/api/users/password-reset-token/*", "/api/users/disclaimer")
                .and()
                .ignoring().antMatchers(HttpMethod.PUT, "/api/users/password");
    }

    private AuthenticationFailureHandler failureLoginHandler() {
        return (request, response, exception) -> {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(exception.getMessage());
            response.getWriter().flush();
        };
    }

    @Bean
    public AuthenticationSuccessHandler successLoginHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().println("{}");
        };
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("x-requested-with");
        configuration.addAllowedHeader("x-xsrf-token");
        configuration.addAllowedHeader("x-auth-token");
        configuration.addAllowedHeader("Content-Type");
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTION"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SwitchUserFilter switchUserFilter() {
        SwitchUserFilter filter = new SwitchUserFilter();
        filter.setUserDetailsService(userDetailsService());
        filter.setTargetUrl("/");
        return filter;
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(singletonList(provider));
    }

    @Bean
    public UserDetailsService userDetailsService(ManageUser userManager) {
        return (username) -> {
            User user;

            try {
                user = userManager.findEnabledByEmail(username);
            } catch (UserMissingException e) {
                throw new UsernameNotFoundException("User " + username + " is not found");
            }

            return new AuthenticatedUserDetails(user);
        };
    }
}