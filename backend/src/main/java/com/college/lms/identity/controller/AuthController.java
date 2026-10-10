package com.college.lms.identity.controller;

import com.college.lms.identity.dto.*;
import com.college.lms.identity.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication endpoints.
 *
 * <p>All endpoints return JSON. The CSRF cookie (XSRF-TOKEN) is set by
 * Spring Security's CsrfFilter and must be echoed as a request header
 * (X-XSRF-TOKEN) on all state-changing requests from the frontend.
 *
 * <p>Authentication state is maintained via an HttpOnly session cookie (LMS_SESSION).
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Registration, login, password management, and email verification")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new student account.
     * Email/password accounts require email verification before login is permitted.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new student account")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Authenticate with email and password.
     * On success, sets an HttpOnly session cookie.
     */
    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public UserResponse login(@Valid @RequestBody LoginRequest request,
                              HttpServletRequest httpRequest) {
        return authService.login(request, httpRequest);
    }

    /**
     * Verify an email address using the token from the verification email.
     * Accepts the token as a query parameter so the link works directly in email clients.
     */
    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address using token from verification email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully. You can now log in."));
    }

    /**
     * Request a password reset email.
     * Always returns 200 regardless of whether the email exists (anti-enumeration).
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset email")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.getOrDefault("email", "").trim();
        authService.forgotPassword(email);
        return ResponseEntity.ok(Map.of(
            "message", "If an account with that email exists, a reset link has been sent."
        ));
    }

    /**
     * Reset the password using the token from the reset email.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token from reset email")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password has been reset successfully."));
    }

    /**
     * Returns the currently authenticated user's profile.
     * Used by the frontend to rehydrate auth state on page load.
     */
    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user")
    public UserResponse getCurrentUser(@AuthenticationPrincipal UserDetails principal) {
        return authService.getCurrentUser(principal.getUsername());
    }

    /**
     * Logout endpoint.
     * Handled by Spring Security's logout filter (configured in SecurityConfig).
     * This placeholder exists for OpenAPI documentation.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate session")
    public ResponseEntity<Map<String, String>> logout() {
        // Actual logout handled by Spring Security LogoutFilter.
        // This method will not be reached under normal operation.
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}
