package com.hotel.reservation.exception.customExceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
