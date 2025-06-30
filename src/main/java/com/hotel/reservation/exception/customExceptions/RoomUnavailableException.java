package com.hotel.reservation.exception.customExceptions;

public class RoomUnavailableException extends RuntimeException {
    public RoomUnavailableException(String message) {
        super(message);
    }
}
