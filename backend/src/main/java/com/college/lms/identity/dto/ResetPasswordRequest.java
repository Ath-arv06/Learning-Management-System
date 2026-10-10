package com.college.lms.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request body for POST /api/v1/auth/reset-password. */
public record ResetPasswordRequest(

    @NotBlank(message = "Token is required")
    String token,

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 128, message = "Password must be 8-128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#^])[A-Za-z\\d@$!%*?&_\\-#^]{8,}$",
        message = "Password must contain uppercase, lowercase, digit, and special character"
    )
    String newPassword

) {}
