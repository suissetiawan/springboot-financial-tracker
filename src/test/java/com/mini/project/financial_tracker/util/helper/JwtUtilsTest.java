package com.mini.project.financial_tracker.util.helper;

import com.mini.project.financial_tracker.entity.User;
import com.mini.project.financial_tracker.util.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private User testUser;
    private final String secret = "de9a0f54130e7f095570c44968b2877def66cdd0cffd85f92450bb1f90e6eacb";
    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", secret);
        ReflectionTestUtils.setField(jwtUtils, "expiration", 3600);
        jwtUtils.init();

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);
    }

    @Test
    void generateAndValidateAccessToken_ShouldSucceed() {
        String token = jwtUtils.generateAccessToken(testUser);
        assertNotNull(token);
        assertTrue(jwtUtils.validateAccessToken(token, testUser));
        assertEquals(testUser.getId().toString(), jwtUtils.extractUserIdFromAccessToken(token));
    }


    @Test
    void validateAccessToken_WithWrongUser_ShouldReturnFalse() {
        String token = jwtUtils.generateAccessToken(testUser);
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        assertFalse(jwtUtils.validateAccessToken(token, anotherUser));
    }

    @Test
    void validateAccessToken_WithExpiredToken_ShouldReturnFalse() {
        ReflectionTestUtils.setField(jwtUtils, "expiration", -10);
        String token = jwtUtils.generateAccessToken(testUser);
        assertFalse(jwtUtils.validateAccessToken(token, testUser));
    }


    @Test
    void validateAccessToken_WithMalformedToken_ShouldReturnFalse() {
        assertFalse(jwtUtils.validateAccessToken("malformed-token", testUser));
    }


    @Test
    void validateAccessToken_WithNullUserId_ShouldReturnFalse() {
        SecretKey accessKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // Use a token with no subject
        String token = Jwts.builder()
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
        assertFalse(jwtUtils.validateAccessToken(token, testUser));
    }


    @Test
    void validateAccessToken_WithNullInput_ShouldReturnFalse() {
        assertFalse(jwtUtils.validateAccessToken(null, testUser));
    }


    @Test
    void validateAccessToken_WithMismatchingUserId_ShouldReturnFalse() {
        String token = jwtUtils.generateAccessToken(testUser);
        User userWithSameIdButDifferentRef = new User();
        userWithSameIdButDifferentRef.setId(UUID.randomUUID()); // different ID
        assertFalse(jwtUtils.validateAccessToken(token, userWithSameIdButDifferentRef));
    }
}
