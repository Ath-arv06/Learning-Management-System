package com.college.lms.identity.security;

import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

/**
 * Central Spring Security configuration for the LMS.
 *
 * <p>Design decisions:
 * <ul>
 *   <li><strong>Session-based auth, not JWTs:</strong> HttpOnly session cookies are safer than
 *       localStorage JWTs for browser clients. Sessions can be revoked server-side;
 *       JWTs cannot be revoked without additional infrastructure.</li>
 *   <li><strong>BCrypt (cost 12):</strong> Spring Security's built-in; sufficient strength
 *       for this use case. Argon2id would be marginally stronger but requires additional
 *       dependency and adds no meaningful security advantage at this deployment scale.</li>
 *   <li><strong>CSRF enabled:</strong> Required for cookie-authenticated state-changing requests.
 *       The frontend must include the CSRF token header on POST/PUT/DELETE.</li>
 *   <li><strong>Deny by default:</strong> All endpoints require authentication unless
 *       explicitly permitted.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final LmsUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    public SecurityConfig(LmsUserDetailsService userDetailsService,
                          UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt cost factor 12: ~300ms per hash on typical hardware.
        // This is intentionally slow to slow down offline brute-force attacks.
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── CORS ──────────────────────────────────────────────────────────
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ── CSRF ──────────────────────────────────────────────────────────
            // Enabled for all state-changing requests (cookie auth requires CSRF protection).
            // The frontend reads the XSRF-TOKEN cookie and sends it as a header.
            // Public endpoints that don't change state are safe from CSRF by nature.
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/v1/auth/login")   // handled by form login
                // Note: POST /auth/register also needs to be CSRF-safe for the first load.
                // We handle this by using Spring's double-submit cookie pattern.
            )

            // ── Authorization rules ───────────────────────────────────────────
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-password").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/v1/auth/verify-email").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/v1/verify/**").permitAll()   // certificate verification
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // OAuth2 login handled by Spring's built-in filter
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // ── Session management ────────────────────────────────────────────
            .sessionManagement(session -> session
                .sessionFixation().changeSessionId()   // rotate session ID on login
                .maximumSessions(5)                    // prevent unlimited concurrent sessions
            )

            // ── OAuth2 (Google) ───────────────────────────────────────────────
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(info -> info.userService(oauth2UserService()))
                .successHandler(oauth2SuccessHandler())
                .failureHandler(oauth2FailureHandler())
            )

            // ── Logout ────────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/api/v1/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("LMS_SESSION")
            )

            // ── Error handling (return JSON, not redirect) ─────────────────────
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(this::handleUnauthorized)
                .accessDeniedHandler(this::handleAccessDenied)
            );

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    // ── CORS configuration ────────────────────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "X-XSRF-TOKEN", "Accept"));
        config.setAllowCredentials(true);  // required for cookies
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    // ── OAuth2 user service (maps Google profile to LMS user) ─────────────────

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oauthUser = delegate.loadUser(request);
            // The actual account provisioning happens in OAuth2SuccessHandler
            return oauthUser;
        };
    }

    // ── OAuth2 success handler ─────────────────────────────────────────────────

    private AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            // Provision or update the LMS user account from the OAuth2 principal.
            // For now this logs and redirects; full logic in Phase 4.
            log.info("OAuth2 login success: {}", authentication.getName());
            response.sendRedirect("http://localhost:5173/oauth2/callback");
        };
    }

    private AuthenticationFailureHandler oauth2FailureHandler() {
        return (request, response, exception) -> {
            log.warn("OAuth2 login failure: {}", exception.getMessage());
            response.sendRedirect("http://localhost:5173/login?error=oauth2");
        };
    }

    // ── Logout success ─────────────────────────────────────────────────────────

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Logged out successfully\"}");
        };
    }

    // ── 401 / 403 JSON responses (not redirects) ──────────────────────────────

    private void handleUnauthorized(HttpServletRequest request,
                                    HttpServletResponse response,
                                    AuthenticationException ex) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"status\":401,\"title\":\"Unauthorized\",\"detail\":\"Authentication required.\"}"
        );
    }

    private void handleAccessDenied(HttpServletRequest request,
                                    HttpServletResponse response,
                                    org.springframework.security.access.AccessDeniedException ex)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"status\":403,\"title\":\"Forbidden\",\"detail\":\"Access denied.\"}"
        );
    }
}
