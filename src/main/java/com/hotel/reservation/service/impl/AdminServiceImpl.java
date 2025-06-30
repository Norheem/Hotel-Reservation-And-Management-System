package com.hotel.reservation.service.impl;

import com.hotel.reservation.auth.service.JwtService;
import com.hotel.reservation.entity.TokenVerification;
import com.hotel.reservation.entity.User;
import com.hotel.reservation.entity.UserSession;
import com.hotel.reservation.entity.enums.Gender;
import com.hotel.reservation.exception.customExceptions.*;
import com.hotel.reservation.payload.request.LoginRequest;
import com.hotel.reservation.payload.request.RegistrationRequest;
import com.hotel.reservation.payload.response.*;
import com.hotel.reservation.repository.UserRepository;
import com.hotel.reservation.repository.UserSessionRepository;
import com.hotel.reservation.service.AdminService;
import com.hotel.reservation.service.EmailService;
import com.hotel.reservation.service.TokenVerificationService;
import com.hotel.reservation.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.hotel.reservation.entity.enums.Role.ADMIN;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenVerificationService tokenVerificationService;

    private final EmailService emailService;

    private final UserSessionRepository userSessionRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;


    @Override
    public AuthResponse register(RegistrationRequest request, Gender gender) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists, kindly log into your account");
        }

        if (!isValidPassword(request.getPassword())) {
            throw new InvalidPasswordException("Password must be at least 8 characters long and contain at least one special character.");
        }

        // register new customer
        User admin = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .gender(gender)
                .role(ADMIN)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(false)
                .resetToken(null)
                .resetTokenExpiry(null)
                .build();
        User savedNewAdmin = userRepository.save(admin);

        String token = tokenVerificationService.generateVerificationToken(savedNewAdmin);

        String verificationUrl = "http://localhost:8080/api/v2/admin/verify?token=" + token;

        //send email alert
        String emailMessageBody = String.format(
                "Dear %s,\n" +
                        "\n" +
                        "Welcome to Roomify, your ultimate hotel reservation and management platform! To complete your registration and activate your account, please verify your email address by clicking the link below:\n" +
                        "\n" +
                        "To activate your admin account and gain access to your dashboard, please verify your email address by clicking the link below:\n" +
                        "\n" +
                        "Verification Link: %s\n" +
                        "\n" +
                        "If the link doesn’t work, copy and paste the URL into your browser.\n" +
                        "\n" +
                        "This verification step ensures the security of your account and access to admin functionalities.\n" +
                        "\n" +
                        "If you did not sign up as an admin on Roomify, please ignore this email.\n" +
                        "\n" +
                        "For any assistance, feel free to contact us at support@roomify.com.\n" +
                        "\n" +
                        "Thank you for choosing Roomify!\n" +
                        "\n" +
                        "Best regards,\n" +
                        "The Roomify Team\n",
                savedNewAdmin.getFirstName(),
                verificationUrl
        );


        EmailDetails sendTokenForRegistration = EmailDetails.builder()
                .recipient(request.getEmail())
                .subject("Verify Your Roomify Account")
                .messageBody(emailMessageBody)
                .build();
        emailService.sendEmailToken(sendTokenForRegistration);


        return AuthResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .registrationInfo(RegistrationInfo.builder()
                        .firstName(savedNewAdmin.getFirstName())
                        .lastName(savedNewAdmin.getLastName())
                        .email(savedNewAdmin.getEmail())
                        .phoneNumber(savedNewAdmin.getPhoneNumber())
                        .role(savedNewAdmin.getRole())
                        .build())
                .token(null)
                .build();
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[!@#$%^&*()].*");
    }


    @Override
    public String verifyAdmin(String token) {
        TokenVerification verifyToken = tokenVerificationService.validateToken(token);


        if (verifyToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired. Please register again.");
        }

        User admin = verifyToken.getUser();
        admin.setActive(true);
        userRepository.save(admin);

        tokenVerificationService.deleteToken(verifyToken);

        String welcomeMessageBody = String.format(
                "Dear %s,\n" +
                        "\n" +
                        "Congratulations! Your Roomify account has been successfully verified and activated.\n" +
                        "Welcome to Roomify, your ultimate hotel reservation and management platform!\n" +
                        "\n" +
                        "You can now log in and start exploring the amazing features we offer.\n" +
                        "\n" +
                        "If you need any assistance, feel free to reach out to our support team at support@roomify.com.\n" +
                        "\n" +
                        "Best regards,\n" +
                        "The Roomify Team\n",
                admin.getFirstName()
        );

        EmailDetails welcomeEmail = EmailDetails.builder()
                .recipient(admin.getEmail())
                .subject("Welcome to Roomify!")
                .messageBody(welcomeMessageBody)
                .build();
        emailService.sendEmailToken(welcomeEmail);

        return "Admin account successfully verified and activated.";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = null;

        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User admin = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + request.getEmail()));

        if (!admin.isActive()) {
            throw new AccountNotVerifiedException("Admin Account not verified. Please check your email.");
        }
        admin.setLastLoginTime(LocalDateTime.now());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtService.generateToken(authentication, admin.getId());

        UserSession session = UserSession.builder()
                .user(admin)
                .loginTime(LocalDateTime.now())
                .build();
        userSessionRepository.save(session);

        return LoginResponse.builder()
                .responseCode(AccountUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(AccountUtils.LOGIN_SUCCESS_MESSAGE)
                .loginInfo(LoginInfo.builder()
                        .firstName((admin.getFirstName()))
                        .email(admin.getEmail())
                        .token(jwtToken)
                        .build())
                .build();
    }

    @Override
    public String logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format");
        }

        String token = authHeader.substring(7);

        Long adminId = jwtService.extractUserIdFromToken(token);
        if (adminId == null) {
            throw new InvalidTokenException("Invalid token: User ID not found");
        }
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", adminId));
        user.setLastLogoutTime(LocalDateTime.now());

        UserSession session = userSessionRepository.findFirstByUserIdOrderByLoginTimeDesc(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", adminId));

        session.setLogoutTime(LocalDateTime.now());
        session.setDuration(Duration.between(session.getLoginTime(), session.getLogoutTime()).getSeconds());
        userSessionRepository.save(session);

        jwtService.blacklistToken(token);

        return "Admin Logged Out Successfully";
    }

    @Override
    public List<RegistrationInfo> getAllUsers(Long customerId, String email) {

        List<User> users;

        if (customerId != null) {
            users = userRepository.findById(customerId)
                    .map(Collections::singletonList)
                    .orElse(Collections.emptyList());
        } else if (email != null && !email.isEmpty()) {
            users = userRepository.findUserByEmail(email);
        } else {
            users = userRepository.findAll();
        }

        return users.stream().map(user -> new RegistrationInfo(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        )).collect(Collectors.toList());
    }
}
