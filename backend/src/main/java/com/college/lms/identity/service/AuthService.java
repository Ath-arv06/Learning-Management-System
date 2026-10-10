package com.college.lms.identity.service;

import com.college.lms.identity.dto.*;
import com.college.lms.identity.entity.User;

public interface AuthService {
    UserResponse register(RegisterRequest request);
    UserResponse login(LoginRequest request, jakarta.servlet.http.HttpServletRequest httpRequest);
    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(ResetPasswordRequest request);
    UserResponse getCurrentUser(String email);
}
