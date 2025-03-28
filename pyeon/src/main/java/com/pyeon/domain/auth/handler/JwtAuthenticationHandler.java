package com.pyeon.domain.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pyeon.global.dto.ApiResponse;
import com.pyeon.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        sendErrorResponse(response, ErrorCode.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        sendErrorResponse(response, ErrorCode.ACCESS_DENIED, HttpServletResponse.SC_FORBIDDEN);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode, int status) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        response.getWriter().write(
            objectMapper.writeValueAsString(ApiResponse.error(errorCode, status))
        );
    }
} 