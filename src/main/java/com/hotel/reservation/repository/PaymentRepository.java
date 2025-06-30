package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Payment;
import com.hotel.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByReservationId(Long reservationId);

    Optional<Payment> findByReservation(Reservation reservation);
}
