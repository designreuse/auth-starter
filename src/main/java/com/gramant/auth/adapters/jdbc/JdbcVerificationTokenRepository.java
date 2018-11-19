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
public class JdbcVerificationTokenRepository implements VerificationTokenRepository {

    private JdbcTemplate jdbcTemplate;
    private static final BeanPropertyRowMapper<VerificationTokenData> VERIFICATION_TOKEN_DATA_MAPPER = new BeanPropertyRowMapper<>(VerificationTokenData.class);

    @Override
    @Transactional
    public void add(VerificationToken token) {
        jdbcTemplate.update("insert into verification_token (token, user_id, expiry_date, token_type) values (?, ?, ?, ?);",
                ps -> {
            ps.setString(1, token.tokenId().asString());
            ps.setString(2, token.user().id().asString());
            ps.setTimestamp(3, Timestamp.valueOf(token.expiryDate()));
            ps.setString(4, token.type().name());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VerificationToken> get(VerificationTokenId tokenId) {
        List<VerificationTokenData> result = jdbcTemplate.query("select token, user_id, expiry_date, token_type from verification_token where token = ?",
                new Object[]{tokenId.asString()},
                VERIFICATION_TOKEN_DATA_MAPPER);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0).asVerificationToken());
    }

    @Override
    @Transactional
    public void remove(VerificationTokenId tokenId) {
        jdbcTemplate.update("delete from verification_token where token = ?", tokenId.asString());
    }

    @Getter
    @Setter
    private static class VerificationTokenData {
        private String token;
        private String userId;
        private LocalDateTime expiryDate;
        private String tokenType;

        VerificationToken asVerificationToken() {
            return new VerificationToken(VerificationTokenType.valueOf(tokenType), VerificationTokenId.of(token), User.builder().id(UserId.of(userId)).build(), expiryDate);
        }
    }
}
