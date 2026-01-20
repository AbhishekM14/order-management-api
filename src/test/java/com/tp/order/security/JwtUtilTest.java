package com.tp.order.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        // 256-bit Base64-encoded secret (REQUIRED for HS256)
        "jwt.secret=ZmFrZVNlY3JldEtleUZha2VTZWNyZXRLZXlGYWtlU2VjcmV0S2V5",
        "jwt.expiration=3600000" // 1 hour
})
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void generateToken_shouldCreateValidJwt() {
        String token = jwtUtil.generateToken("abhi");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtil.generateToken("abhi");

        String username = jwtUtil.extractUsername(token);

        assertEquals("john", username);
    }

    @Test
    void extractExpiration_shouldReturnFutureDate() {
        String token = jwtUtil.generateToken("abhi");

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_shouldReturnTrue_forValidUser() {
        String token = jwtUtil.generateToken("abhi");

        UserDetails userDetails =
                User.withUsername("abhi")
                        .password("password")
                        .roles("USER")
                        .build();

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalse_forDifferentUser() {
        String token = jwtUtil.generateToken("abhi");

        UserDetails userDetails =
                User.withUsername("abhi")
                        .password("password")
                        .roles("USER")
                        .build();

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {
        // Short-lived token (1 ms)
        JwtUtil shortLivedJwtUtil = new JwtUtil();
        TestUtils.injectField(shortLivedJwtUtil, "secret",
                "ZmFrZVNlY3JldEtleUZha2VTZWNyZXRLZXlGYWtlU2VjcmV0S2V5");
        TestUtils.injectField(shortLivedJwtUtil, "expiration", 1L);

        String token = shortLivedJwtUtil.generateToken("abhi");

        Thread.sleep(5); // ensure expiration

        UserDetails userDetails =
                User.withUsername("abhi")
                        .password("password")
                        .roles("USER")
                        .build();

        assertFalse(shortLivedJwtUtil.validateToken(token, userDetails));
    }
}