package com.gramant.auth.adapters.jdbc;

import com.gramant.auth.domain.*;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class JdbcVerificationTokenRepository implements VerificationTokenRepository {

    private JdbcTemplate jdbcTemplate;
    private static final RowMapper<VerificationToken> VERIFICATION_TOKEN_MAPPER = new VerificationTokenMapper();

    @Override
    @Transactional
    public void add(VerificationToken token) {
        jdbcTemplate.update("insert into verification_token (token, user_id, expiry_date, token_type) values (?, ?, ?, ?);",
                ps -> {
            ps.setString(1, token.tokenId().asString());
            ps.setString(2, token.userId().asString());
            ps.setTimestamp(3, Timestamp.valueOf(token.expiryDate()));
            ps.setString(4, token.type().name());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VerificationToken> get(VerificationTokenId tokenId) {
        List<VerificationToken> result = jdbcTemplate.query("select token, user_id, expiry_date, token_type from verification_token where token = ?",
                new Object[]{tokenId.asString()},
                VERIFICATION_TOKEN_MAPPER);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    @Transactional
    public void remove(VerificationTokenId tokenId) {
        jdbcTemplate.update("delete from verification_token where token = ?", tokenId.asString());
    }

    private static class VerificationTokenMapper implements RowMapper<VerificationToken> {

        @Override
        public VerificationToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new VerificationToken(
                    VerificationTokenType.valueOf(rs.getString("token_type")),
                    VerificationTokenId.of(rs.getString("token")),
                    UserId.of(rs.getString("user_id")),
                    rs.getTimestamp("expiry_date").toLocalDateTime()
            );
        }
    }
}
