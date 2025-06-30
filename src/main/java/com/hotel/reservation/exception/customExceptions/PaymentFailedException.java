package com.hotel.reservation.exception.customExceptions;

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
