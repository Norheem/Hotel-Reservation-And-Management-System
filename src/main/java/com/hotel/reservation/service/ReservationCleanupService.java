package com.hotel.reservation.service;

public interface ReservationCleanupService {

    void cancelUnpaidReservationsAfter12Hours();
}
