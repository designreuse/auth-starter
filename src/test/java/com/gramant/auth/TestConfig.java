package com.gramant.auth;

import com.gramant.auth.app.RoleProvider;
import com.gramant.auth.domain.PrivilegeId;
import com.gramant.auth.domain.PrivilegedRole;
import com.gramant.auth.domain.RoleId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public RoleProvider roleProvider() {
        return new RoleProvider.Default(
                new PrivilegedRole(new RoleId("A"), Arrays.asList(PrivilegeId.EDIT_USERS, new PrivilegeId("TEST"))),
                new PrivilegedRole(new RoleId("B"), Collections.singletonList(PrivilegeId.EDIT_USERS)),
                new PrivilegedRole(new RoleId("C"), Collections.emptyList()),
                new PrivilegedRole(new RoleId("D"), Collections.emptyList()),
                new PrivilegedRole(new RoleId("E"), Collections.emptyList())
        );
    }
}
