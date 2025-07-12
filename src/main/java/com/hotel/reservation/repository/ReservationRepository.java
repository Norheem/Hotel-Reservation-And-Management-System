package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id = :roomId AND " +
            "(r.checkInDate < :checkOut AND r.checkOutDate > :checkIn)")
    long countByRoomIdAndDateRange(@Param("roomId") Long roomId,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut);

    List<Reservation> findByUserId(long userId);

    List<Reservation> findByRoomId(Long roomId);

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.reservationStatus IN :statuses")
    List<Reservation> findByRoomIdAndStatusIn(@Param("roomId") Long roomId, @Param("statuses") List<ReservationStatus> statuses);

    @EntityGraph(attributePaths = "payment")
    List<Reservation> findByReservationStatusAndCreatedAtBefore(ReservationStatus status, LocalDateTime time);


}
