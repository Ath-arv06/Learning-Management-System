package com.college.lms.identity.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AuthServiceImpl utility methods.
 * Uses no Spring context — pure unit tests.
 */
class AuthServiceImplTest {

    private final AuthServiceImpl authService;

    AuthServiceImplTest() {
        // Instantiate with nulls for dependencies not needed in these tests
        this.authService = new AuthServiceImpl(
            null, null, null, null, null, null, null
        );
    }

    @Test
    void hashToken_returnsDeterministicHash() {
        String token = "test-token-value";
        String hash1 = authService.hashToken(token);
        String hash2 = authService.hashToken(token);
        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(token);
        assertThat(hash1).hasSize(64); // SHA-256 = 32 bytes = 64 hex chars
    }

    @Test
    void hashToken_differentInputsProduceDifferentHashes() {
        assertThat(authService.hashToken("token-a"))
            .isNotEqualTo(authService.hashToken("token-b"));
    }

    @Test
    void hashToken_isNotReversible() {
        // There is no inverse of SHA-256 — just verify that the hash
        // does not contain the plaintext token
        String plain = "mysecrettoken";
        String hash = authService.hashToken(plain);
        assertThat(hash).doesNotContain(plain);
    }
}
