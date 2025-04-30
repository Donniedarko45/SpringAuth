package com.auth.service;

import com.auth.dto.LoginRequest;
import com.auth.dto.SignupRequest;

public interface AuthService {
    String signup(SignupRequest request);
    String login(LoginRequest request);
    String verifyOtp(String email, String otp);
    void sendOtp(String email);
    boolean validatePassword(String password, String confirmPassword);
} 