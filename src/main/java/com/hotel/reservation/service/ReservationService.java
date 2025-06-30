package com.hotel.reservation.service;

import com.hotel.reservation.payload.request.ReservationRequest;
import com.hotel.reservation.payload.response.ReservationInfo;
import com.hotel.reservation.payload.response.ReservationResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request, Long userId);

    void checkInReservation(Long reservationId);

    boolean isRoomAvailable(LocalDate checkIn, LocalDate checkOut, Long roomId);

    ReservationResponse updateReservation(Long reservationId, ReservationRequest request, Long userId);

    List<ReservationInfo> getUserReservations(Long userId);

    String cancelReservation(Long reservationId);

    void checkOutReservation(Long reservationId);
}
