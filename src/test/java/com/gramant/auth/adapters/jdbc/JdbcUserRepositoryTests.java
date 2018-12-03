package com.gramant.auth.adapters.jdbc;

import com.gramant.auth.TestConfig;
import com.gramant.auth.app.RoleProvider;
import com.gramant.auth.domain.*;
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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
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

    @Autowired
    private RoleProvider roleProvider;

    @Test
    public void beansCreated() {
        assertNotNull(userRepository);
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/testdata/init.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/testdata/clean.sql"),
            @Sql("/testdata/users-records.sql")
    })
    public void getUser() {
        Optional<User> user = userRepository.get(UserId.of("user-1"));
        assertTrue(user.isPresent());
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/testdata/init.sql"),
            @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/testdata/clean.sql"),
            @Sql("/testdata/users-records.sql"),
            @Sql("/testdata/authorities-records.sql")
    })
    public void listWithRoles() {
        Collection<User> list = userRepository.list();
        assertEquals(2, list.size());
        Map<UserId, User> map = list.stream().collect(toMap(User::id, i -> i));
        User user1 = map.get(UserId.of("user-1"));
        User user2 = map.get(UserId.of("user-2"));

        assertEquals(4, user1.roles().size());
        assertEquals(4, user1.roles().size());

        assertEquals(roleProvider.role(new RoleId("A")).get(), user1.roles().get(0));
        assertEquals(roleProvider.role(new RoleId("B")).get(), user1.roles().get(1));
        assertEquals(roleProvider.role(new RoleId("C")).get(), user1.roles().get(2));
        assertEquals(roleProvider.role(new RoleId("D")).get(), user1.roles().get(3));

        assertEquals(roleProvider.role(new RoleId("B")).get(), user2.roles().get(0));
        assertEquals(roleProvider.role(new RoleId("C")).get(), user2.roles().get(1));
        assertEquals(roleProvider.role(new RoleId("D")).get(), user2.roles().get(2));
        assertEquals(roleProvider.role(new RoleId("E")).get(), user2.roles().get(3));
    }
}
