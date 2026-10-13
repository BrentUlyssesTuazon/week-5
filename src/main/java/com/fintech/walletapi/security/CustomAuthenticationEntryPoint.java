package com.fintech.walletapi.security;

import tools.jackson.databind.ObjectMapper;
import com.fintech.walletapi.exception.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ErrorResponseDTO error = new ErrorResponseDTO(
                401, "Unauthorized", "Access denied: No token or invalid token provided"
        );
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}