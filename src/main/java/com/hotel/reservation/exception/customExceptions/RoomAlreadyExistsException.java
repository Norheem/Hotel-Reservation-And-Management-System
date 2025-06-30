package com.hotel.reservation.exception.customExceptions;

public class RoomAlreadyExistsException extends RuntimeException {
    public RoomAlreadyExistsException(String message) {
        super(message);
    }
}
