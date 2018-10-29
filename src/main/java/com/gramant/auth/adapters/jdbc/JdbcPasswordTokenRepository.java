package com.gramant.auth.adapters.jdbc;

import com.gramant.auth.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class JdbcPasswordTokenRepository implements PasswordTokenRepository {

    private JdbcTemplate jdbcTemplate;
    private static final BeanPropertyRowMapper<PasswordResetTokenData> PASSWORD_RESET_DATA_MAPPER = new BeanPropertyRowMapper<>(PasswordResetTokenData.class);

    @Override
    @Transactional
    public void add(PasswordResetToken passwordResetToken) {
        jdbcTemplate.update("insert into password_reset_token (token, user_id, expiry_date) values (?, ?, ?);",
                ps -> {
            ps.setString(1, passwordResetToken.tokenId().asString());
            ps.setString(2, passwordResetToken.user().id().asString());
            ps.setTimestamp(3, Timestamp.valueOf(passwordResetToken.expiryDate()));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PasswordResetToken> get(PasswordResetTokenId tokenId) {
        List<PasswordResetTokenData> result = jdbcTemplate.query("select token, user_id, expiry_date from password_reset_token where token = ?",
                new Object[]{tokenId.asString()},
                PASSWORD_RESET_DATA_MAPPER);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0).asPasswordResetToken());
    }

    @Override
    @Transactional
    public void remove(PasswordResetTokenId tokenId) {
        jdbcTemplate.update("delete from password_reset_token where token = ?", tokenId.asString());
    }

    @Getter
    @Setter
    private static class PasswordResetTokenData {
        private String token;
        private String userId;
        private LocalDateTime expiryDate;

        PasswordResetToken asPasswordResetToken() {
            return new PasswordResetToken(PasswordResetTokenId.of(token), User.builder().id(UserId.of(userId)).build(), expiryDate);
        }
    }
}
