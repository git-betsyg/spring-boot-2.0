package com.example.demo.security;

import com.example.demo.enums.APIExceptionCode;
import com.example.demo.exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Refresh Token 的创建、校验、轮换与 Cookie 读写。
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${jwt.refresh-token-expiry:604800}")
    private long refreshTokenExpirySeconds;

    @Value("${jwt.refresh-token-cookie-name:refresh_token}")
    private String cookieName;

    @Value("${jwt.cookie-secure:false}")
    private boolean cookieSecure;

    public String create(String username) {
        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = Instant.now().plusSeconds(refreshTokenExpirySeconds);
        jdbcTemplate.update(
                "INSERT INTO refresh_tokens (username, token, expires_at) VALUES (?, ?, ?)",
                username, token, Timestamp.from(expiresAt));
        return token;
    }

    /**
     * 校验旧 token，删除后签发新 token（轮换，防止重复使用）。
     *
     * @return 用户名
     */
    public RotateResult rotate(String token) {
        return jdbcTemplate.query(
                "SELECT username, expires_at FROM refresh_tokens WHERE token = ?",
                rs -> {
                    if (!rs.next()) {
                        throw new APIException(APIExceptionCode.REFRESH_TOKEN_INVALID.getErrorCode(),
                                APIExceptionCode.REFRESH_TOKEN_INVALID.getErrorMessage());
                    }

                    String username = rs.getString("username");
                    Instant expiresAt = rs.getTimestamp("expires_at").toInstant();
                    if (expiresAt.isBefore(Instant.now())) {
                        jdbcTemplate.update("DELETE FROM refresh_tokens WHERE token = ?", token);
                        throw new APIException(APIExceptionCode.REFRESH_TOKEN_EXPIRED.getErrorCode(),
                                APIExceptionCode.REFRESH_TOKEN_EXPIRED.getErrorMessage());
                    }

                    jdbcTemplate.update("DELETE FROM refresh_tokens WHERE token = ?", token);
                    String newToken = create(username);
                    return new RotateResult(username, newToken);
                },
                token);
    }

    public void revoke(String token) {
        jdbcTemplate.update("DELETE FROM refresh_tokens WHERE token = ?", token);
    }

    public void writeCookie(HttpServletResponse response, String token) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie(token, (int) refreshTokenExpirySeconds).toString());
    }

    public void clearCookie(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildCookie("", 0).toString());
    }

    private ResponseCookie buildCookie(String value, int maxAgeSeconds) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/token")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax")
                .build();
    }

    @lombok.Value
    public static class RotateResult {
        String username;
        String refreshToken;
    }

}
