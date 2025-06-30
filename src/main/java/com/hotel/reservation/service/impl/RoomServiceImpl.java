package com.hotel.reservation.service.impl;

import com.hotel.reservation.entity.Room;
import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.entity.enums.RoomType;
import com.hotel.reservation.exception.customExceptions.RoomAlreadyExistsException;
import com.hotel.reservation.exception.customExceptions.RoomNotFoundException;
import com.hotel.reservation.exception.customExceptions.RoomUnavailableException;
import com.hotel.reservation.payload.request.RoomRequest;
import com.hotel.reservation.payload.response.RoomInfo;
import com.hotel.reservation.payload.response.RoomResponse;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import com.hotel.reservation.service.RoomService;
import com.hotel.reservation.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final ReservationRepository reservationRepository;


    @Override
    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new RoomAlreadyExistsException("Room number already exists.");
        }

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .price(request.getPrice())
                .roomStatus(request.getRoomStatus())
                .roomCapacity(request.getRoomCapacity())
                .build();

        Room savedRoom = roomRepository.save(room);
        return mapToRoomResponse(savedRoom);
    }

    @Override
    public RoomResponse updateRoom(Long roomId, RoomRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " +roomId));

        room.setRoomType(request.getRoomType());
        room.setPrice(request.getPrice());
        room.setRoomStatus(request.getRoomStatus());
        room.setRoomCapacity(request.getRoomCapacity());

        Room updatedRoom = roomRepository.save(room);
        return mapToRoomResponse(updatedRoom);
    }

    public void deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with id: " + roomId));

        roomRepository.delete(room);
    }

//    @Override
//    public String deleteRoom(Long roomId) {
//        if (!roomRepository.existsById(roomId)) {
//            throw new RoomNotFoundException("Room not found.");
//        }
//        roomRepository.deleteById(roomId);
//
//        return "Room with ID " + roomId +" has been deleted successfully.";
//    }

    @Override
    public RoomResponse getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        return mapToRoomResponse(room);
    }

    @Override
    public List<RoomInfo> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::mapToRoomInfo)
                .collect(Collectors.toList());
    }

    public List<RoomInfo> getAllRoomsByStatus(RoomStatus roomStatus) {
        return roomRepository.findByRoomStatus(roomStatus)
                .stream()
                .map(this::mapToRoomInfo)
                .collect(Collectors.toList());
    }

    public List<RoomInfo> getAllRoomsByType(RoomType roomType) {
        return roomRepository.findByRoomType(roomType)
                .stream()
                .map(this::mapToRoomInfo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = roomRepository.findAvailableRooms(checkIn, checkOut);

        if (rooms.isEmpty()) {
            throw new RoomUnavailableException("No rooms available for selected dates. Try another date.");
        }

        return rooms;
    }


//    @Override
//    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
//        List<Reservation> existingReservations = reservationRepository.findReservationsByRoomIdAndDateRange(roomId, checkIn, checkOut);
//
//        System.out.println("🔍 Checking availability for Room " + roomId);
//        System.out.println("➡️ Check-In: " + checkIn + " | Check-Out: " + checkOut);
//        System.out.println("🔎 Found " + existingReservations.size() + " reservations");
//
//
//        for (Reservation res : existingReservations) {
//            System.out.println("🚨 Reserved from: " + res.getCheckIn() + " to " + res.getCheckOut());
//        }
//
//
//        if (existingReservations.isEmpty()) {
//            return true;
//        } else {
//            LocalDate nextAvailableDate = existingReservations.stream()
//                    .map(Reservation::getCheckOut)
//                    .max(LocalDate::compareTo)
//                    .orElse(checkOut)
//                    .plusDays(1); // Available the next day after the last check-out
//
//            throw new RoomOccupiedException("Room is currently occupied. It will be available on " + nextAvailableDate);
//        }
//    }



//    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
//        return roomRepository.countOverlappingReservations(roomId, checkIn, checkOut) == 0;
//    }


    private RoomInfo mapToRoomInfo(Room room) {
        return RoomInfo.builder()
                .roomId(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .price(room.getPrice())
                .roomStatus(room.getRoomStatus())
                .roomCapacity(room.getRoomCapacity())
                .build();
    }


    private RoomResponse mapToRoomResponse(Room room) {
        return RoomResponse.builder()
                .responseCode(AccountUtils.SUCCESS_CODE)
                .responseMessage(AccountUtils.SUCCESS_MESSAGE)
                .roomInfo(RoomInfo.builder()
                        .roomId(room.getId())
                        .roomNumber(room.getRoomNumber())
                        .roomType(room.getRoomType())
                        .price(room.getPrice())
                        .roomStatus(room.getRoomStatus())
                        .roomCapacity(room.getRoomCapacity())
                        .build())
                .build();
    }
}
