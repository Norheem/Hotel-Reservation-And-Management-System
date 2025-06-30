package com.hotel.reservation.exception.customExceptions;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
