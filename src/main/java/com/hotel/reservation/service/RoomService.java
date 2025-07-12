package com.hotel.reservation.service;

import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.entity.enums.RoomType;
import com.hotel.reservation.payload.request.RoomRequest;
import com.hotel.reservation.payload.response.RoomInfo;
import com.hotel.reservation.payload.response.RoomResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface RoomService {

    RoomResponse createRoom(RoomRequest request);

    RoomResponse updateRoom(Long roomId, RoomRequest request);

    void deleteRoom(Long roomId);

    RoomResponse getRoomById(Long roomId);

    List<RoomInfo> getAllRooms();

    List<RoomInfo> getAllRoomsByStatus(RoomStatus roomStatus);

    List<RoomInfo> getAllRoomsByType(RoomType roomType);

//    List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut);

    List<RoomInfo> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType roomType);
}
