package com.hotel.reservation.exception.customExceptions;

public class RoomOccupiedException extends RuntimeException {
    public RoomOccupiedException(String message) {
        super(message);
    }
}
