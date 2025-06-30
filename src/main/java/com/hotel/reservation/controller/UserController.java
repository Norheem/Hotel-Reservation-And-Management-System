package com.hotel.reservation.controller;


import com.hotel.reservation.entity.enums.Gender;
import com.hotel.reservation.payload.request.LoginRequest;
import com.hotel.reservation.payload.request.RegistrationRequest;
import com.hotel.reservation.payload.response.AuthResponse;
import com.hotel.reservation.payload.response.LoginResponse;
import com.hotel.reservation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegistrationRequest request,
            @RequestParam Gender gender) {
        return ResponseEntity.ok(userService.register(request, gender));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        return ResponseEntity.ok(userService.verifyUser(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.logout(authHeader));
    }
}
