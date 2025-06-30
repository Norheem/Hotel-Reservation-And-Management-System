package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Room;
import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);

    List<Room> findByRoomTypeAndRoomStatus(RoomType roomType, RoomStatus roomStatus);

    List<Room> findByRoomStatus(RoomStatus roomStatus);

    List<Room> findByRoomType(RoomType roomType);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id = :roomId AND " +
    " ((r.checkIn <= :checkOut AND r.checkOut >= :checkIn))")
    int countOverlappingReservations(@Param("roomId") Long roomId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut);


    @Query("SELECT r FROM Room r WHERE r.roomStatus = 'AVAILABLE' " +
            "AND r.id NOT IN (SELECT res.room.id FROM Reservation res " +
            "WHERE (:checkIn < res.checkOut AND :checkOut > res.checkIn))")
    List<Room> findAvailableRooms(@Param("checkIn") LocalDate checkIn,
                                  @Param("checkOut") LocalDate checkOut);
}
