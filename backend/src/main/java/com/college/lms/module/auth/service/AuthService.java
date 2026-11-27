package com.college.lms.module.auth.service;

import com.college.lms.common.security.UserPrincipal;
import com.college.lms.module.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request, HttpServletResponse response);
    AuthResponse loginGoogle(GoogleLoginRequest request, HttpServletResponse response);
    AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
    void logout(HttpServletResponse response);
    UserDto getCurrentUser(UserPrincipal principal);
}
