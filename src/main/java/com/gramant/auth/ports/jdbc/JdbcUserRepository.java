package com.gramant.auth.ports.jdbc;

import com.gramant.auth.app.RoleProvider;
import com.gramant.auth.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Repository
@AllArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<UserData> USER_DATA_ROW_MAPPER = new BeanPropertyRowMapper<>(UserData.class);

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private RoleProvider roleProvider;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        List<UserData> users = jdbcTemplate.query(
                "SELECT id, email, password, enabled, last_login FROM users WHERE email = ?",
                new Object[] {email},
                USER_DATA_ROW_MAPPER);

        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0).asUser(getRoles(users.get(0).id)));
    }

    @Override
    @Transactional(readOnly = true)
    // todo: select in one query (join-map)
    public Optional<User> get(UserId userId) {
        List<UserData> users = jdbcTemplate.query(
                "SELECT id, email, password, enabled, last_login FROM users WHERE id = ?",
                new Object[] {userId.asString()},
                USER_DATA_ROW_MAPPER);

        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0).asUser(getRoles(userId.asString())));
    }

    @Override
    @Transactional
    public User add(User user) {
        jdbcTemplate.update("insert into users (id, email, password, enabled, last_login) values (?, ?, ?, true, null)",
                user.getId().asString(), user.getEmail(), user.getPassword());

        jdbcTemplate.batchUpdate("insert into authorities (user_id, role_id) values (?, ?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PrivilegedRole role = user.getRoles().get(i);
                ps.setString(1, user.getId().asString());
                ps.setString(2, role.id().asString());
            }

            @Override
            public int getBatchSize() {
                return user.getRoles().size();
            }
        });
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> list() {
        List<UserData> users = jdbcTemplate.query(
                "SELECT id, email, password, enabled, last_login FROM users ORDER BY email",
                USER_DATA_ROW_MAPPER);

        return users.stream().map(userData -> userData.asUser(getRoles(userData.getId()))).collect(toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getAll(Collection<UserId> userIds) {
        List<UserData> users = namedParameterJdbcTemplate.query(
                "SELECT id, email, password, enabled, last_login FROM users WHERE id IN (:ids) ORDER BY email",
                Collections.singletonMap("ids", userIds.stream().map(UserId::asString).collect(toList())),
                USER_DATA_ROW_MAPPER);

        return users.stream().map(userData -> userData.asUser(getRoles(userData.getId()))).collect(toList());
    }

    @Override
    @Transactional
    public void updateAll(List<User> users) {
        jdbcTemplate.batchUpdate("UPDATE users SET enabled = ? WHERE id = ?", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setBoolean(1, user.isEnabled());
                ps.setString(2, user.getId().asString());
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
        jdbcTemplate.update("update users set email = ?, password = ?, enabled = ?, last_login = ? where id  = ?",
                user.getEmail(), user.getPassword(), user.isEnabled(), user.getLastLogin(), user.getId().asString());
    }

    private List<PrivilegedRole> getRoles(String userId) {
        List<RoleId> roleIds = jdbcTemplate.queryForList("select role_id from authorities where user_id = ?", new Object[]{userId}, RoleId.class);
        return roleIds.stream().map(roleProvider::role).map(o -> o.orElse(PrivilegedRole.unknown())).collect(toList());
    }

    @Getter
    @Setter
    static class UserData {
        private String id;
        private String email;
        private String password;
        private boolean enabled;

        User asUser(List<PrivilegedRole> roles) {
            return new User(UserId.of(id), email, password, enabled, roles, null);
        }
    }
}
