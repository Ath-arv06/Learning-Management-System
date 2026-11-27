package com.college.lms.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private final String secret = "c2VjdXJlX2xvbmdfcmFuZG9tX2tleV9mb3Jfb3B0b3V0X2xtc18yMDI2X3VuaXZlcnNpdHlfcGxhdGZvcm1fc2VjcmV0IQ==";

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, 900000, 604800000);
    }

    @Test
    @DisplayName("Should generate and validate a valid JWT access token")
    void shouldGenerateAndValidateToken() {
        UserPrincipal principal = new UserPrincipal(
                1L,
                "user-uuid-1234",
                "student@university.edu",
                "hashedPass",
                "Alice Smith",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );

        String token = tokenProvider.generateAccessToken(principal);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("student@university.edu", tokenProvider.getEmailFromToken(token));
    }

    @Test
    @DisplayName("Should generate valid refresh token")
    void shouldGenerateRefreshToken() {
        UserPrincipal principal = new UserPrincipal(
                2L,
                "user-uuid-5678",
                "instructor@university.edu",
                "hashedPass",
                "Bob Jones",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))
        );

        String refreshToken = tokenProvider.generateRefreshToken(principal);

        assertNotNull(refreshToken);
        assertTrue(tokenProvider.validateToken(refreshToken));
        assertEquals("instructor@university.edu", tokenProvider.getEmailFromToken(refreshToken));
    }

    @Test
    @DisplayName("Should reject malformed or tampered token")
    void shouldRejectTamperedToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalidpayload.invalidsignature";
        assertFalse(tokenProvider.validateToken(invalidToken));
    }
}
