package com.mini.project.financial_tracker.utils;

import java.util.*;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.mini.project.financial_tracker.entity.User;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;
    private SecretKey accessKey;

    @Value("${jwt.refresh.secret}")
    private String refreshSecret;
    private SecretKey refreshKey;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.refresh.expiration}")
    private int refreshExpiration;

    @PostConstruct
    public void init() {
        this.accessKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user){

        return Jwts.builder()
                .setId(UUID.randomUUID().toString()) // jti
                .setSubject(user.getId().toString()) // sub
                .setIssuer("auth-service")
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user){
        return Jwts.builder()
                .setId(UUID.randomUUID().toString()) // jti
                .setSubject(user.getId().toString()) // sub
                .setIssuer("auth-service")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration * 1000))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Claim All
    private Claims extractAccessToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Claims extractRefreshToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // --------- ACCESS TOKEN ---------
    public String extractUserIdFromAccessToken(String token) {
        return extractAccessToken(token).getSubject();
    }

    private boolean isAccessTokenExpired(String token) {
        return extractAccessToken(token).getExpiration().before(new Date());
    }

    public boolean validateAccessToken(String token, User user) {
        final String userId = extractUserIdFromAccessToken(token);
        return userId != null && !isAccessTokenExpired(token) && userId.equals(user.getId().toString());
    }

    // --------- REFRESH TOKEN ---------
    public String extractUserIdFromRefreshToken(String token) {
        return extractRefreshToken(token).getSubject();
    }
    
    public String extractJtiFromRefreshToken(String token) {
        return extractRefreshToken(token).getId();
    }

    private boolean isRefreshTokenExpired(String token) {
        return extractRefreshToken(token).getExpiration().before(new Date());
    }

    public boolean validateRefreshToken(String token) {
        final String userId = extractUserIdFromRefreshToken(token);
        return userId != null && !isRefreshTokenExpired(token);
    }
}
