package com.college.lms.identity.dto;

import java.time.Instant;
import java.util.Set;

/**
 * Public user representation returned from the API.
 * Never includes passwordHash, googleId, or internal tokens.
 */
public record UserResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    String fullName,
    String phone,
    String avatarUrl,
    String status,
    Set<String> roles,
    boolean emailVerified,
    Instant createdAt
) {}
