package com.mini.project.financial_tracker.util.helper;

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

    @Value("${jwt.expiration}")
    private int expiration;

    @PostConstruct
    public void init() {
        this.accessKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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


    // Claim All
    private Claims extractAccessToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
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
        try {
            final String userId = extractUserIdFromAccessToken(token);
            return userId != null && !isAccessTokenExpired(token) && userId.equals(user.getId().toString());
        } catch (Exception e) {
            return false;
        }
    }

}
