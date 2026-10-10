package com.college.lms.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/v1/auth/register.
 *
 * Password requirements:
 * - Minimum 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character
 *
 * These constraints are validated server-side; client-side validation is advisory only.
 */
public record RegisterRequest(

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be 1-100 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be 1-100 characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#^])[A-Za-z\\d@$!%*?&_\\-#^]{8,}$",
        message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    String password

) {}
