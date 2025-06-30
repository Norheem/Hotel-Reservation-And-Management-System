package com.hotel.reservation.service;

import com.hotel.reservation.entity.enums.Gender;
import com.hotel.reservation.payload.request.LoginRequest;
import com.hotel.reservation.payload.request.RegistrationRequest;
import com.hotel.reservation.payload.response.AuthResponse;
import com.hotel.reservation.payload.response.LoginResponse;
import com.hotel.reservation.payload.response.RegistrationInfo;

import java.util.List;

public interface AdminService {

    AuthResponse register(RegistrationRequest request, Gender gender);

    String verifyAdmin(String token);

    LoginResponse login(LoginRequest request);

    String logout(String authHeader);

    List<RegistrationInfo> getAllUsers(Long customerId, String email);
}
