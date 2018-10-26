package com.gramant.auth;

import com.gramant.auth.app.ManageUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.Cookie;
import javax.sql.DataSource;
import java.util.Arrays;

import static java.util.Collections.singletonList;

/**
 * Spring Security Config
 */
@Configuration
@Import(UserDetailsPreloader.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;
    private final AuthProperties authProperties;
    @Autowired private UserDetailsService userDetailsService;

    private static String[] allowedOrigins = new String[] {"http://localhost:80"};

    @Autowired
    public WebSecurityConfig(DataSource dataSource, AuthProperties authProperties) {
        this.dataSource = dataSource;
        this.authProperties = authProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().configurationSource(corsConfigurationSource());
        http.authorizeRequests().antMatchers(
                "/auth/login",
                "/auth/users",
                "/auth/check"
        ).permitAll();
        http.authorizeRequests().antMatchers("/api/**").authenticated();
        http.logout()
                .permitAll().logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
        http.formLogin()
                .loginProcessingUrl("/auth/login")
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
        http.rememberMe()
                .userDetailsService(userDetailsService)
                .tokenValiditySeconds(authProperties.getRememberMeTokenValiditySeconds());

        if (authProperties.getEnablePersistentLogins()) {
            http.rememberMe()
                    .key(authProperties.getRemeberMeKey())
                    .rememberMeServices(persistentTokenBasedRememberMeServices());
        } else {
            http.rememberMe()
                    .userDetailsService(userDetailsService)
                    .tokenRepository(inMempersistentTokenRepository());

        }

        http.addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring().antMatchers("/auth/check/**")
                .and()
                .ignoring().antMatchers(HttpMethod.POST, "/auth/users", "/auth/users/password-reset-token")
                .and()
                .ignoring().antMatchers(HttpMethod.GET, "/auth/users/password-reset-token/*")
                .and()
                .ignoring().antMatchers(HttpMethod.PUT, "/auth/users/password");
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
        filter.setUserDetailsService(userDetailsService);
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
    @ConditionalOnProperty(name ="auth-starter.enable-persistent-logins", havingValue = "true")
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();

        jdbcTokenRepository.setCreateTableOnStartup(false);
        jdbcTokenRepository.setDataSource(dataSource);

        return jdbcTokenRepository;
    }

    @Bean
    @ConditionalOnProperty(name ="auth-starter.enable-persistent-logins", havingValue = "false")
    public PersistentTokenRepository inMempersistentTokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

    @Bean
    @ConditionalOnProperty(name ="auth-starter.enable-persistent-logins", havingValue = "true")
    public RememberMeServices persistentTokenBasedRememberMeServices() {
        PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(authProperties.getRemeberMeKey(), userDetailsService, persistentTokenRepository());

        services.setAlwaysRemember(true);
        services.setTokenValiditySeconds(authProperties.getRememberMeTokenValiditySeconds());

        return services;
    }
}