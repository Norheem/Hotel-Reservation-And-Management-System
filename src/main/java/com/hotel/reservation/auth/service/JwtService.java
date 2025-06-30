package com.hotel.reservation.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtService {


    private final String SECRET_KEY = "MIHcAgEBBEIBHOAQ2vcFrnvHoQMRcGrdeHR6dRr+vidGtW8wnGA8UcuZkzUDZ4KrGeOiJQNCF74XwfPrQMA+Ri4tnqusc2gYN+2gBwYFK4EEACOhgYkDgYYABADwc4U4Yx2CEmjqRRmgOvkJzgjiX9InzJf+DQZJwJof72ldl/OQmiZm0p7weJtDt1sxg3zi5Fi+yns77EJrSKrc5QBUh5bzP9HQfUXGdc9h3cGc6CNKia9kAltOLZCM+L0W+GBYtXzaCwLV0eFV0Vpsp7iD+RtnKhMFiPRZJI2iWCzUwQ==";


    private final Long jwtExpirationDate = 86400000L;

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private Key key(){
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);

        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(Authentication authentication, Long userId) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() +  jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(currentDate)
                .setExpiration(expirationDate)
                .signWith(key())
                .compact();
    }

    public String getUserName(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getPayload();

        return claims.getSubject();
    }

    public Long extractUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parse(token);

            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody();
    }
}
