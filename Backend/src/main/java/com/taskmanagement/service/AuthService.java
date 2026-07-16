package com.taskmanagement.service;

import com.taskmanagement.dto.request.LoginRequest;
import com.taskmanagement.dto.request.RefreshTokenRequest;
import com.taskmanagement.dto.request.RegisterRequest;
import com.taskmanagement.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(String email);
}
