package com.hotel.reservation.service.impl;

import com.hotel.reservation.auth.service.JwtAuthenticationFilter;
import com.hotel.reservation.auth.service.JwtService;
import com.hotel.reservation.entity.TokenVerification;
import com.hotel.reservation.entity.User;
import com.hotel.reservation.entity.UserSession;
import com.hotel.reservation.entity.enums.Gender;
import com.hotel.reservation.exception.customExceptions.*;
import com.hotel.reservation.payload.request.LoginRequest;
import com.hotel.reservation.payload.request.RegistrationRequest;
import com.hotel.reservation.payload.request.UpdateProfileRequest;
import com.hotel.reservation.payload.response.*;
import com.hotel.reservation.repository.UserRepository;
import com.hotel.reservation.repository.UserSessionRepository;
import com.hotel.reservation.service.EmailService;
import com.hotel.reservation.service.TokenVerificationService;
import com.hotel.reservation.service.UserService;
import com.hotel.reservation.utils.AccountUtils;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

import static com.hotel.reservation.entity.enums.Role.CUSTOMER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenVerificationService tokenVerificationService;

    private final EmailService emailService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final UserSessionRepository userSessionRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final HttpServletRequest servletRequest;




    @Override
    public AuthResponse register(RegistrationRequest request, Gender gender) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists, kindly log into your account");
        }

        if (!isValidPassword(request.getPassword())) {
            throw new InvalidPasswordException("Password must be at least 8 characters long and contain at least one special character.");
        }

        // register new customer
        User newUser = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .address(request.getAddress())
                .gender(gender)
                .role(CUSTOMER)
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(false)
                .resetToken(null)
                .resetTokenExpiry(null)
                .build();
        User savedNewUser = userRepository.save(newUser);

        String token = tokenVerificationService.generateVerificationToken(savedNewUser);

        String verificationUrl = "http://localhost:8080/api/v1/auth/verify?token=" + token;

        //send email alert
        String emailMessageBody = String.format(
                "Dear %s,\n" +
                        "\n" +
                        "Welcome to Roomify, your ultimate hotel reservation and management platform! To complete your registration and activate your account, please verify your email address by clicking the link below:\n" +
                        "\n" +
                        "Verification Link: %s\n" +
                        "\n" +
                        "If the link doesn’t work, copy and paste the URL into your browser.\n" +
                        "\n" +
                        "This verification step ensures the security of your account.\n" +
                        "\n" +
                        "If you did not sign up for Roomify, please ignore this email.\n" +
                        "\n" +
                        "For any assistance, feel free to contact us at support@roomify.com.\n" +
                        "\n" +
                        "Thank you for choosing Roomify!\n" +
                        "\n" +
                        "Best regards,\n" +
                        "The Roomify Team\n",
                savedNewUser.getFirstName(),
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
                        .firstName(savedNewUser.getFirstName())
                        .lastName(savedNewUser.getLastName())
                        .email(savedNewUser.getEmail())
                        .phoneNumber(savedNewUser.getPhoneNumber())
                        .role(savedNewUser.getRole())
                        .build())
                .token(null)
                .build();
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 8 && password.matches(".*[!@#$%^&*()].*");
    }

    @Override
    public String verifyUser(String token) {
        TokenVerification verificationToken = tokenVerificationService.validateToken(token);


        if (verificationToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expired. Please register again.");
        }

        User user = verificationToken.getUser();
        user.setActive(true);
        userRepository.save(user);

        tokenVerificationService.deleteToken(verificationToken);

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
                user.getFirstName()
        );

        EmailDetails welcomeEmail = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("Welcome to Roomify!")
                .messageBody(welcomeMessageBody)
                .build();
        emailService.sendEmailToken(welcomeEmail);

        return "User account successfully verified and activated.";
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

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        if (!user.isActive()) {
            throw new AccountNotVerifiedException("Account not verified. Please check your email.");
        }
        user.setLastLoginTime(LocalDateTime.now());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtService.generateToken(authentication, user.getId());

        UserSession session = UserSession.builder()
                .user(user)
                .loginTime(LocalDateTime.now())
                .build();

        userSessionRepository.save(session);

        return LoginResponse.builder()
                .responseCode(AccountUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(AccountUtils.LOGIN_SUCCESS_MESSAGE)
                .loginInfo(LoginInfo.builder()
                        .firstName((user.getFirstName()))
                        .email(user.getEmail())
                        .token(jwtToken)
                        .build())
                .build();
    }

    @Override
    public AuthResponse updateUser(UpdateProfileRequest request) {
        String token = jwtAuthenticationFilter.getTokenFromRequest(servletRequest);

        if (jwtService.isBlacklisted(token)) {
            throw new SecurityException("The token is invalid or has been blacklisted, Pls log back in");
        }

        String email = jwtService.getUserName(token);

        User existingUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        if (existingUser != null) {
            Optional.ofNullable(request.getFirstName()).ifPresent(existingUser::setFirstName);
            Optional.ofNullable(request.getLastName()).ifPresent(existingUser::setLastName);
            Optional.ofNullable(request.getEmail()).ifPresent(existingUser::setEmail);
            Optional.ofNullable(request.getAddress()).ifPresent(existingUser::setAddress);
            Optional.ofNullable(request.getPhoneNumber()).ifPresent(existingUser::setPhoneNumber);


            User savedUser = userRepository.save(existingUser);

            return AuthResponse.builder()
                    .responseCode(AccountUtils.UPDATE_USER_SUCCESSFUL_CODE)
                    .responseMessage(AccountUtils.UPDATE_USER_SUCCESSFUL_MESSAGE)
                    .registrationInfo(RegistrationInfo.builder()
                            .firstName(savedUser.getFirstName())
                            .lastName(savedUser.getLastName())
                            .email(savedUser.getEmail())
                            .build())
                    .token(null)
                    .build();
        }
        return null;
    }

    @Override
    public String logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token format");
        }

        String token = authHeader.substring(7);

        Long userId = jwtService.extractUserIdFromToken(token);
        if (userId == null) {
            throw new InvalidTokenException("Invalid token: User ID not found");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setLastLogoutTime(LocalDateTime.now());

        UserSession session = userSessionRepository.findFirstByUserIdOrderByLoginTimeDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", userId));

        session.setLogoutTime(LocalDateTime.now());
        session.setDuration(Duration.between(session.getLoginTime(), session.getLogoutTime()).getSeconds());
        userSessionRepository.save(session);

        jwtService.blacklistToken(token);

        return "Customer Logged Out Successfully";
    }
}
