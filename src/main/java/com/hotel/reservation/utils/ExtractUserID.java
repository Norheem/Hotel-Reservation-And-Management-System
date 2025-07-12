package com.hotel.reservation.utils;

import com.hotel.reservation.auth.service.JwtAuthenticationFilter;
import com.hotel.reservation.auth.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExtractUserID {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;

    public Long getUserIdFromToken(HttpServletRequest servletRequest) {
        String token = jwtAuthenticationFilter.getTokenFromRequest(servletRequest);

        // Check if the token is null or empty
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token is empty");
        }

        // Validate the token
        if (!jwtService.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        if (jwtService.isBlacklisted(token)) {
            throw new RuntimeException("Token is blacklisted");
        }

        Long userId = jwtService.extractUserIdFromToken(token);
        if (userId == null) {
            throw new RuntimeException("Unauthorized: Unable to extract userId from token");
        }

        return userId;
    }
}
