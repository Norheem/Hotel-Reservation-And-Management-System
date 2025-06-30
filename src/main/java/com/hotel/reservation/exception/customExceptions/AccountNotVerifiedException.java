package com.hotel.reservation.exception.customExceptions;

public class AccountNotVerifiedException extends RuntimeException {

    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
