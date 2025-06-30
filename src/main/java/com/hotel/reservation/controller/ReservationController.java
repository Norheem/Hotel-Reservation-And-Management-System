package com.hotel.reservation.controller;

import com.hotel.reservation.entity.Room;
import com.hotel.reservation.entity.User;
import com.hotel.reservation.payload.request.ReservationRequest;
import com.hotel.reservation.payload.response.ReservationInfo;
import com.hotel.reservation.payload.response.ReservationResponse;
import com.hotel.reservation.service.ReservationService;
import com.hotel.reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final RoomService roomService;

    @PostMapping("/book")
    public ResponseEntity<ReservationInfo> createReservation(@AuthenticationPrincipal User user,
                                                      @RequestBody ReservationRequest request) {
        request.setCustomerId(user.getId());
        return ResponseEntity.ok(reservationService.createReservation(request, user.getId()).getReservationInfo());
    }

    @PostMapping("/checkin/{reservationId}")
    public ResponseEntity<String> checkIn(@PathVariable Long reservationId) {
        try {
            reservationService.checkInReservation(reservationId);
            return ResponseEntity.ok("Check-in successful. Room status updated to OCCUPIED.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/view-reservation")
    public ResponseEntity<List<ReservationInfo>> viewReservationDetail(@AuthenticationPrincipal User user) {
       return ResponseEntity.ok(reservationService.getUserReservations(user.getId()));
    }

    @PutMapping("/update/{reservationId}")
    public ResponseEntity<ReservationResponse> updateReservation(@AuthenticationPrincipal User user,
                                                                 @PathVariable Long reservationId,
                                                                 @RequestBody ReservationRequest request){
        return ResponseEntity.ok(reservationService.updateReservation(reservationId, request, user.getId()));
    }

    @DeleteMapping("/delete/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.cancelReservation(reservationId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam("checkIn") LocalDate checkIn,
            @RequestParam("checkOut") LocalDate checkOut) {
        return ResponseEntity.ok(roomService.getAvailableRooms(checkIn, checkOut));
    }

    @PostMapping("/checkout/{reservationId}")
    public ResponseEntity<String> checkOut(@PathVariable Long reservationId) {
        try {
            reservationService.checkOutReservation(reservationId);
            return ResponseEntity.ok("Check-out successful. Room status updated to AVAILABLE and receipt sent.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



//
//    @GetMapping("/availability")
//    public ResponseEntity<?> checkRoomAvailability(
//            @RequestParam Long roomId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
//        try {
//            boolean isAvailable = roomService.isRoomAvailable(roomId, checkIn, checkOut);
//            return ResponseEntity.ok(Collections.singletonMap("available", isAvailable));
//        } catch (RoomOccupiedException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body(Collections.singletonMap("message", e.getMessage()));
//        }
//    }


}
