package com.example.testbanking.authservice.service;

import com.example.testbanking.authservice.dto.AuthRequest;
import com.example.testbanking.authservice.dto.AuthResponse;
import com.example.testbanking.authservice.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
}
