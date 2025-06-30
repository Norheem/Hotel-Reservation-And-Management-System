package com.hotel.reservation.exception;


import com.hotel.reservation.exception.customExceptions.*;
import com.hotel.reservation.payload.response.ApiResponse;
import com.hotel.reservation.payload.response.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CentralizedExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<AuthResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        AuthResponse response = AuthResponse.builder()
                .responseCode("400")
                .responseMessage(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle AccountNotVerifiedException
    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<AuthResponse> handleAccountNotVerified(AccountNotVerifiedException ex) {
        AuthResponse response = AuthResponse.builder()
                .responseCode("400")
                .responseMessage(ex.getMessage())
                .build();
        return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
    }

    // Handle InvalidTokenException
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException ex) {
        AuthResponse response = AuthResponse.builder()
                .responseCode("400")
                .responseMessage(ex.getMessage())
                .build();
        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<AuthResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        AuthResponse response = AuthResponse.builder()
                .responseCode("400")
                .responseMessage(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateMerchandiseException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateCropException(DuplicateMerchandiseException ex) {
        ApiResponse<String> response = new ApiResponse<>(ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception ex) {
        ApiResponse<String> response = new ApiResponse<>("Error🚨🚨: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}
