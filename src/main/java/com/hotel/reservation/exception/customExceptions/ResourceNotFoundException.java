package com.hotel.reservation.exception.customExceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(resourceName + " with ID "+ resourceId + " not found");
    }

    public ResourceNotFoundException(String resourceName) {
        super(resourceName +" not found");
    }
}
