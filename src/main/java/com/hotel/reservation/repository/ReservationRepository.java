package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id = :roomId AND " +
            "(r.checkIn < :checkOut AND r.checkOut > :checkIn)")
    long countByRoomIdAndDateRange(@Param("roomId") Long roomId,
                                   @Param("checkIn") LocalDate checkIn,
                                   @Param("checkOut") LocalDate checkOut);

    List<Reservation> findByUserId(long userId);

}
