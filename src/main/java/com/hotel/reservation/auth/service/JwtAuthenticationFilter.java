package com.hotel.reservation.auth.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        try {
            if (!StringUtils.hasText(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtService.isBlacklisted(token)) {
                setUnauthorizedResponse(response, "Token has been blacklisted. Please log in again.");
                return;
            }

            if (!jwtService.validateToken(token)) {
                setUnauthorizedResponse(response, "Invalid or expired token. Please log in.");
                return;
            }

            String username = jwtService.getUserName(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (Exception e) {
            setUnauthorizedResponse(response, "An error occurred while processing the token. Please try again.");
            return;
        }

        filterChain.doFilter(request, response);

    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private void setUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"timestamp\": \"%s\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"%s\"}",
                new java.util.Date(), message
        ));
    }
}
