package com.example.testbanking.authservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String STATUS_KEY = "status";
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";
    private static final String PATH_KEY = "path";
    private static final String UNAUTHORIZED_ERROR = "Unauthorized";

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = createResponseBody(request);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

    @NotNull
    private Map<String, Object> createResponseBody(HttpServletRequest request) {
        final Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP_KEY, LocalDateTime.now().toString());
        body.put(STATUS_KEY, HttpServletResponse.SC_UNAUTHORIZED);
        body.put(ERROR_KEY, UNAUTHORIZED_ERROR);
        body.put(MESSAGE_KEY, "JWT token is invalid, expired, or missing. Please re-authenticate.");
        body.put(PATH_KEY, request.getServletPath());
        return body;
    }
}
