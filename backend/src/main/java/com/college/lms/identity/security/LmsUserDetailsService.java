package com.college.lms.identity.security;

import com.college.lms.identity.entity.User;
import com.college.lms.identity.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads a user by email for Spring Security's authentication manager.
 * The "username" here is the user's email address.
 *
 * <p>This service bridges Spring Security's UserDetails model with our
 * domain User entity. It maps our Role entities to Spring authorities
 * with the "ROLE_" prefix convention.
 */
@Service
public class LmsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public LmsUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPasswordHash() != null ? user.getPasswordHash() : "")
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(user.getStatus() == com.college.lms.identity.entity.UserStatus.SUSPENDED)
            .credentialsExpired(false)
            .disabled(user.getStatus() == com.college.lms.identity.entity.UserStatus.PENDING_VERIFICATION)
            .build();
    }

    /**
     * Resolves the database ID for the currently authenticated user from their email (username).
     * Used by controllers that need the user's Long ID for service calls.
     */
    @Transactional(readOnly = true)
    public Long getUserId(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
            .getId();
    }
}
