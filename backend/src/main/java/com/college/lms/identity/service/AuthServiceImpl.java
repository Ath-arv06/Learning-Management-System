package com.college.lms.identity.service;

import com.college.lms.identity.dto.*;
import com.college.lms.identity.entity.*;
import com.college.lms.identity.repository.*;
import com.college.lms.shared.exception.BusinessRuleException;
import com.college.lms.shared.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Core authentication service.
 *
 * <p>Security design notes:
 * <ul>
 *   <li>Registration and forgot-password always return the same success-like message
 *       regardless of whether the email exists (prevents user enumeration).</li>
 *   <li>Tokens are random URL-safe Base64 strings; only their SHA-256 hash is stored.</li>
 *   <li>Session ID is rotated on login to prevent session fixation.</li>
 *   <li>Password reset tokens are single-use and 1-hour TTL.</li>
 * </ul>
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailVerificationTokenRepository evtRepository;
    private final PasswordResetTokenRepository prtRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Value("${app.auth.password-reset-token-expiry-hours:1}")
    private int passwordResetExpiryHours;

    @Value("${app.auth.email-verification-token-expiry-hours:24}")
    private int emailVerificationExpiryHours;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           EmailVerificationTokenRepository evtRepository,
                           PasswordResetTokenRepository prtRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.evtRepository = evtRepository;
        this.prtRepository = prtRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    // ── Registration ─────────────────────────────────────────────────────────

    @Override
    public UserResponse register(RegisterRequest request) {
        // Normalize email to lowercase
        String normalizedEmail = request.email().toLowerCase().trim();

        // Anti-enumeration: if email already exists, we still send a 200 response,
        // but we do NOT create a duplicate account. Log for monitoring.
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.info("Registration attempted with already-registered email (not disclosed to caller)");
            // Return a placeholder response that looks like success but does not reveal
            // whether the email exists. The real user will receive no new email.
            return buildEnumerationSafeResponse();
        }

        Role studentRole = roleRepository.findByName("STUDENT")
            .orElseThrow(() -> new IllegalStateException("STUDENT role not seeded"));

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setRoles(Set.of(studentRole));

        user = userRepository.save(user);

        // Generate and send verification email
        String plainToken = generateSecureToken();
        saveEmailVerificationToken(user, plainToken);
        emailService.sendEmailVerification(user.getEmail(), user.getFirstName(), plainToken);

        log.info("New student registered: {} (id={})", normalizedEmail, user.getId());
        return toUserResponse(user);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Override
    public UserResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String normalizedEmail = request.email().toLowerCase().trim();

        // Spring Security's AuthenticationManager performs credential validation.
        // It throws BadCredentialsException on failure (handled by GlobalExceptionHandler).
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );

        // Session fixation protection: invalidate old session, create new one
        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = httpRequest.getSession(true);

        // Bind the authentication to the new session
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        newSession.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            context
        );

        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User", normalizedEmail));

        log.info("User logged in: {} (id={})", normalizedEmail, user.getId());
        return toUserResponse(user);
    }

    // ── Email verification ────────────────────────────────────────────────────

    @Override
    public void verifyEmail(String token) {
        String tokenHash = hashToken(token);
        EmailVerificationToken evt = evtRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new BusinessRuleException("Invalid or expired verification link."));

        if (!evt.isValid()) {
            throw new BusinessRuleException("This verification link has expired or already been used.");
        }

        User user = evt.getUser();
        user.setEmailVerifiedAt(Instant.now());
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        evt.setUsed(true);
        evtRepository.save(evt);

        log.info("Email verified for user id={}", user.getId());
    }

    // ── Forgot password ───────────────────────────────────────────────────────

    @Override
    public void forgotPassword(String email) {
        String normalizedEmail = email.toLowerCase().trim();

        // Always complete without revealing whether the email exists
        userRepository.findByEmail(normalizedEmail).ifPresent(user -> {
            // Invalidate existing reset tokens
            prtRepository.invalidateAllForUser(user.getId());

            String plainToken = generateSecureToken();
            savePasswordResetToken(user, plainToken);
            emailService.sendPasswordReset(user.getEmail(), user.getFirstName(), plainToken);

            log.info("Password reset token sent to user id={}", user.getId());
        });
    }

    // ── Reset password ────────────────────────────────────────────────────────

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = hashToken(request.token());
        PasswordResetToken prt = prtRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new BusinessRuleException("Invalid or expired reset link."));

        if (!prt.isValid()) {
            throw new BusinessRuleException("This reset link has expired or already been used.");
        }

        User user = prt.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        prt.setUsed(true);
        prtRepository.save(prt);

        log.info("Password reset for user id={}", user.getId());
    }

    // ── Current user ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", email));
        return toUserResponse(user);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    String hashToken(String plainToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private void saveEmailVerificationToken(User user, String plainToken) {
        evtRepository.invalidateAllForUser(user.getId());

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setTokenHash(hashToken(plainToken));
        token.setExpiresAt(Instant.now().plus(emailVerificationExpiryHours, ChronoUnit.HOURS));
        evtRepository.save(token);
    }

    private void savePasswordResetToken(User user, String plainToken) {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setTokenHash(hashToken(plainToken));
        token.setExpiresAt(Instant.now().plus(passwordResetExpiryHours, ChronoUnit.HOURS));
        prtRepository.save(token);
    }

    UserResponse toUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());

        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getFullName(),
            user.getPhone(),
            user.getAvatarUrl(),
            user.getStatus().name(),
            roles,
            user.isEmailVerified(),
            user.getCreatedAt()
        );
    }

    /**
     * Returns a response that looks like a successful registration but does not
     * reveal whether the email already exists in the system.
     */
    private UserResponse buildEnumerationSafeResponse() {
        return new UserResponse(
            null, null, null, null,
            "If this email is not registered, you will receive a verification email shortly.",
            null, null, "PENDING_VERIFICATION",
            Set.of("STUDENT"), false, Instant.now()
        );
    }
}
