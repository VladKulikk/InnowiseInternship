package com.innowise.internship.authentificationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    @Value("${app.jwt.access-token.expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;

    public JwtProvider(@Value("${app.jwt.access-secret}") String jwtAccessSecret, @Value("${app.jwt.refresh-secret}") String jwtRefreshSecret) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(jwtAccessSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(jwtRefreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String login) {
        final Date now = new Date();
        final Date accessExpiration =  new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(login)
                .issuedAt(now)
                .expiration(accessExpiration)
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(String login) {
        final Date now = new Date();
        final Date refreshExpiration =  new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
                .subject(login)
                .issuedAt(now)
                .expiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, jwtAccessSecret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    public String getLoginFromRefreshToken(String token) {
        return getClaims(token, jwtRefreshSecret).getSubject();
    }

    private boolean validateToken(String token, SecretKey secret) {
        try{
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private Claims getClaims(String token,  SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
