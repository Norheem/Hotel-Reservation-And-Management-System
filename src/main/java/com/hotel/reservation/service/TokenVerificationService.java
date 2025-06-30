package com.hotel.reservation.service;

import com.hotel.reservation.entity.TokenVerification;
import com.hotel.reservation.entity.User;

public interface TokenVerificationService {

    String generateVerificationToken(User user);

    TokenVerification validateToken(String token);

    void deleteToken(TokenVerification token);
}
