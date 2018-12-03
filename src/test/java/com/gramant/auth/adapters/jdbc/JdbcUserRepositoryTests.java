package com.gramant.auth.adapters.jdbc;

import com.gramant.auth.TestConfig;
import com.gramant.auth.domain.User;
import com.gramant.auth.domain.UserId;
import com.gramant.auth.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@Rollback
public class JdbcUserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void beansCreated() {
        assertNotNull(userRepository);
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/testdata/init.sql"),
            @Sql("/testdata/users-records.sql")
    })
    public void getUser() {
        Optional<User> user = userRepository.get(UserId.of("user-1"));
        assertTrue(user.isPresent());
    }
}
