package com.gramant.auth.adapters.jdbc;

import com.gramant.auth.app.RoleProvider;
import com.gramant.auth.domain.*;
import lombok.AllArgsConstructor;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private static final String SELECT_USERS_JOIN_ROLES_SQL = "SELECT u.id AS id, u.email, u.password, u.enabled, u.non_locked as nonLocked, " +
            "a.role_id AS roles_val FROM users u LEFT JOIN authorities a ON u.id = a.user_id ";

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private RoleProvider roleProvider;
    private final ResultSetExtractor<List<User>> USER_WITH_ROLES_EXTRACTOR =
            JdbcTemplateMapperFactory
                    .newInstance()
                    .addKeys("id")
                    .addColumnProperty(
                            "roles_val",
                            ConverterProperty.of((ContextualConverter<String, PrivilegedRole>) (in, context) ->
                                    roleProvider.role(new RoleId(ofNullable(in).orElse(""))).orElse(PrivilegedRole.unknown())))
                    .newResultSetExtractor(User.class);

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        List<User> users = jdbcTemplate.query(SELECT_USERS_JOIN_ROLES_SQL + " WHERE u.email = ?", new Object[]{email},
                USER_WITH_ROLES_EXTRACTOR);
        return extractOne(users);
    }

    @Override
    @Transactional(readOnly = true)
    // todo: select in one query (join-map)
    public Optional<User> get(UserId userId) {
        List<User> users = jdbcTemplate.query(SELECT_USERS_JOIN_ROLES_SQL + " WHERE u.id = ?",
                new Object[]{userId.asString()}, USER_WITH_ROLES_EXTRACTOR);
        return extractOne(users);
    }

    private Optional<User> extractOne(List<User> users) {
        return ofNullable(users).orElse(emptyList()).isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    @Override
    @Transactional
    public User add(User user) {
        jdbcTemplate.update("insert into users (id, email, password, enabled, non_locked, last_login) values (?, ?, ?, ?, ?, ?)",
                user.id().asString(), user.email(), user.password(), user.enabled(), user.nonLocked(), user.lastLogin());

        jdbcTemplate.batchUpdate("insert into authorities (user_id, role_id) values (?, ?)", new AuthoritiesBatchPsSetter(user));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> list() {
        return jdbcTemplate.query(SELECT_USERS_JOIN_ROLES_SQL + " ORDER BY u.email", USER_WITH_ROLES_EXTRACTOR);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getAll(Collection<UserId> userIds) {
        return namedParameterJdbcTemplate.query(SELECT_USERS_JOIN_ROLES_SQL + " WHERE id IN (:ids) ORDER BY email",
                Collections.singletonMap("ids", userIds.stream().map(UserId::asString).collect(toList())),
                USER_WITH_ROLES_EXTRACTOR);
    }

    @Override
    @Transactional
    public void updateAll(List<User> users) {
        jdbcTemplate.batchUpdate("UPDATE users SET non_locked = ? WHERE id = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setBoolean(1, user.nonLocked());
                ps.setString(2, user.id().asString());
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });

    }

    @Override
    @Transactional
    public void update(User user) {
        jdbcTemplate.update("update users set email = ?, password = ?, non_locked = ?, last_login = ? where id  = ?",
                user.email(), user.password(), user.nonLocked(), user.lastLogin(), user.id().asString());

        jdbcTemplate.update("delete from authorities where user_id = ?", user.id().asString());
        jdbcTemplate.batchUpdate("insert into authorities (user_id, role_id) values (?, ?)", new AuthoritiesBatchPsSetter(user));
    }

    @AllArgsConstructor
    private static class AuthoritiesBatchPsSetter implements BatchPreparedStatementSetter {

        private final User user;

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            PrivilegedRole role = user.roles().get(i);
            ps.setString(1, user.id().asString());
            ps.setString(2, role.id().asString());
        }

        @Override
        public int getBatchSize() {
            return user.roles().size();
        }
    }
}
