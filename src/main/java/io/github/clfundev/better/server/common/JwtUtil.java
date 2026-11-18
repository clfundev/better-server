package io.github.clfundev.better.server.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long atExpirationMs;
    private final long rtExpirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.at-expiration-ms}") long atExpirationMs,
                   @Value("${jwt.rt-expiration-ms}") long rtExpirationMs) {
        // HS256 要求密钥 >= 256 bits
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.atExpirationMs = atExpirationMs;
        this.rtExpirationMs = rtExpirationMs;
    }

    public String generateAccessToken(String username) {
        return buildToken(username, atExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, rtExpirationMs);
    }

    private String buildToken(String username, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * 解析token得到用户名（若token不合法则返回null）
     */
    public String getUsernameFromToken(String token) {
        try {
            return parse(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验token
     */
    public boolean isValidToken(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 校验token的签名和有效期（未通过会抛JwtException），解析得到payload
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}