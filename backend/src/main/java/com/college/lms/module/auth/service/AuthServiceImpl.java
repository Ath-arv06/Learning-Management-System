package com.college.lms.module.auth.service;

import com.college.lms.common.exception.BadRequestException;
import com.college.lms.common.exception.ResourceNotFoundException;
import com.college.lms.common.exception.UnauthorizedException;
import com.college.lms.common.security.JwtTokenProvider;
import com.college.lms.common.security.UserPrincipal;
import com.college.lms.common.util.CookieUtils;
import com.college.lms.module.auth.dto.*;
import com.college.lms.module.department.entity.Department;
import com.college.lms.module.department.repository.DepartmentRepository;
import com.college.lms.module.user.entity.*;
import com.college.lms.module.user.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            DepartmentRepository departmentRepository,
            StudentProfileRepository studentProfileRepository,
            InstructorProfileRepository instructorProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.instructorProfileRepository = instructorProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists");
        }

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role specified. Must be ROLE_STUDENT or ROLE_INSTRUCTOR");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName, "Auto-created role")));

        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setRoles(Collections.singleton(role));

        User savedUser = userRepository.save(user);

        // Department resolution (defaults to first available or CSE if not provided)
        Department department = null;
        if (request.getDepartmentCode() != null && !request.getDepartmentCode().isBlank()) {
            department = departmentRepository.findByCode(request.getDepartmentCode())
                    .orElse(null);
        }
        if (department == null) {
            department = departmentRepository.findAll().stream().findFirst()
                    .orElseGet(() -> departmentRepository.save(new Department("CSE", "Computer Science", "Default Dept")));
        }

        if (roleName == RoleName.ROLE_STUDENT) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(savedUser);
            profile.setDepartment(department);
            String studentId = (request.getStudentIdNumber() != null && !request.getStudentIdNumber().isBlank())
                    ? request.getStudentIdNumber()
                    : "STU-" + System.currentTimeMillis() % 1000000;
            profile.setStudentIdNumber(studentId);
            profile.setAdmissionYear(Year.now().getValue());
            studentProfileRepository.save(profile);
        } else if (roleName == RoleName.ROLE_INSTRUCTOR) {
            InstructorProfile profile = new InstructorProfile();
            profile.setUser(savedUser);
            profile.setDepartment(department);
            String empCode = (request.getEmployeeCode() != null && !request.getEmployeeCode().isBlank())
                    ? request.getEmployeeCode()
                    : "FAC-" + System.currentTimeMillis() % 1000000;
            profile.setEmployeeCode(empCode);
            profile.setDesignation(request.getDesignation() != null ? request.getDesignation() : "Assistant Professor");
            profile.setQualification("M.Tech / Ph.D");
            instructorProfileRepository.save(profile);
        }

        UserDto userDto = mapToUserDto(savedUser, department.getName());
        return new AuthResponse("Registration successful. Please log in.", userDto);
    }

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        setAuthCookies(principal, response);

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDto userDto = mapToUserDto(user, null);
        return new AuthResponse("Login successful", userDto);
    }

    @Override
    @Transactional
    public AuthResponse loginGoogle(GoogleLoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(request.getEmail().toLowerCase().trim());
                    newUser.setFirstName(request.getFirstName());
                    newUser.setLastName(request.getLastName());
                    newUser.setAvatarUrl(request.getAvatarUrl());
                    newUser.setAuthProvider(AuthProvider.GOOGLE);
                    newUser.setProviderId(request.getProviderId());
                    newUser.setEmailVerified(true);

                    Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                            .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_STUDENT, "Student Role")));
                    newUser.setRoles(Collections.singleton(studentRole));

                    User created = userRepository.save(newUser);

                    Department dept = departmentRepository.findAll().stream().findFirst()
                            .orElseGet(() -> departmentRepository.save(new Department("CSE", "Computer Science", "Default Dept")));

                    StudentProfile profile = new StudentProfile();
                    profile.setUser(created);
                    profile.setDepartment(dept);
                    profile.setStudentIdNumber("G-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                    profile.setAdmissionYear(Year.now().getValue());
                    studentProfileRepository.save(profile);

                    return created;
                });

        UserPrincipal principal = UserPrincipal.create(user);
        setAuthCookies(principal, response);

        UserDto userDto = mapToUserDto(user, null);
        return new AuthResponse("Google authentication successful", userDto);
    }

    @Override
    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.getCookieValue(request, CookieUtils.REFRESH_TOKEN_COOKIE_NAME)
                .orElseThrow(() -> new UnauthorizedException("Refresh token cookie is missing"));

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String email = tokenProvider.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found for token"));

        UserPrincipal principal = UserPrincipal.create(user);
        setAuthCookies(principal, response);

        UserDto userDto = mapToUserDto(user, null);
        return new AuthResponse("Token refreshed successfully", userDto);
    }

    @org.springframework.beans.factory.annotation.Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    @Override
    public void logout(HttpServletResponse response) {
        ResponseCookie cleanAccess = CookieUtils.createCleanCookie(CookieUtils.ACCESS_TOKEN_COOKIE_NAME, cookieSecure);
        ResponseCookie cleanRefresh = CookieUtils.createCleanCookie(CookieUtils.REFRESH_TOKEN_COOKIE_NAME, cookieSecure);

        response.addHeader("Set-Cookie", cleanAccess.toString());
        response.addHeader("Set-Cookie", cleanRefresh.toString());
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToUserDto(user, null);
    }

    private void setAuthCookies(UserPrincipal principal, HttpServletResponse response) {
        String accessToken = tokenProvider.generateAccessToken(principal);
        String refreshToken = tokenProvider.generateRefreshToken(principal);

        ResponseCookie accessCookie = CookieUtils.createAccessTokenCookie(
                accessToken, tokenProvider.getAccessTokenExpirationMs(), cookieSecure
        );
        ResponseCookie refreshCookie = CookieUtils.createRefreshTokenCookie(
                refreshToken, tokenProvider.getRefreshTokenExpirationMs(), cookieSecure
        );

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    private UserDto mapToUserDto(User user, String departmentName) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toList());

        return new UserDto(
                user.getId(),
                user.getUuid(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getAvatarUrl(),
                roles,
                departmentName
        );
    }
}
