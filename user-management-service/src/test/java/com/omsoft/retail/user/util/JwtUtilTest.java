package com.omsoft.retail.user.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET_KEY = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6"; // 32 chars for HS256

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET_KEY);
    }

    @Test
    void generateToken_returnsNonEmptyToken() {
        String token = jwtUtil.generateToken("user@example.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_returnsSubjectFromToken() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);
        assertEquals(email, jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_returnsTrueWhenUsernameMatches() {
        String email = "user@test.com";
        String token = jwtUtil.generateToken(email);
        assertTrue(jwtUtil.isTokenValid(token, email));
    }

    @Test
    void isTokenValid_returnsFalseWhenUsernameDoesNotMatch() {
        String token = jwtUtil.generateToken("user@test.com");
        assertFalse(jwtUtil.isTokenValid(token, "other@test.com"));
    }
}
