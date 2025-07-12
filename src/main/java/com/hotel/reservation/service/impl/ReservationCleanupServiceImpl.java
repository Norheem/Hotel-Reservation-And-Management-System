package com.hotel.reservation.service.impl;

import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.entity.Room;
import com.hotel.reservation.entity.enums.PaymentStatus;
import com.hotel.reservation.entity.enums.ReservationStatus;
import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import com.hotel.reservation.service.ReservationCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationCleanupServiceImpl implements ReservationCleanupService {

    private final ReservationRepository reservationRepository;

    private final RoomRepository roomRepository;


    @Override
    public void cancelUnpaidReservationsAfter12Hours() {

        LocalDateTime cutoff = LocalDateTime.now().minusHours(12);

        List<Reservation> expired = reservationRepository.findByReservationStatusAndCreatedAtBefore(
                ReservationStatus.BOOKED, cutoff
        );

        for (Reservation reservation : expired) {
            if (reservation.getPayment() != null &&
                    reservation.getPayment().getPaymentStatus() == PaymentStatus.PENDING) {

                reservation.setReservationStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);

                Room room = reservation.getRoom();
                room.setRoomStatus(RoomStatus.AVAILABLE);
                roomRepository.save(room);
            }
        }
    }
}
