package com.example.testbanking.authservice.service;

import com.example.testbanking.authservice.dto.AuthRequest;
import com.example.testbanking.authservice.dto.AuthResponse;
import com.example.testbanking.authservice.dto.RegisterRequest;
import com.example.testbanking.authservice.model.Role;
import com.example.testbanking.authservice.model.User;
import com.example.testbanking.authservice.repository.UserRepository;
import com.example.testbanking.common.exception.BankingException;
import com.example.testbanking.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BankingException(ErrorCode.USER_ALREADY_EXISTS);
        }

        final var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BankingException(ErrorCode.USER_NOT_FOUND));

        final var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
