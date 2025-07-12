package com.hotel.reservation.controller;

import com.hotel.reservation.entity.User;
import com.hotel.reservation.payload.request.ReservationRequest;
import com.hotel.reservation.payload.request.RoomSearchRequest;
import com.hotel.reservation.payload.response.ReservationInfo;
import com.hotel.reservation.payload.response.ReservationResponse;
import com.hotel.reservation.payload.response.RoomInfo;
import com.hotel.reservation.service.ReservationCleanupService;
import com.hotel.reservation.service.ReservationService;
import com.hotel.reservation.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final ReservationCleanupService reservationCleanupService;

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
    public ResponseEntity<List<RoomInfo>> getAvailableRooms(
            @RequestParam("checkIn") LocalDate checkIn,
            @RequestParam("checkOut") LocalDate checkOut) {
        List<RoomInfo> availableRooms = roomService.searchAvailableRooms(checkIn, checkOut, null);
        return ResponseEntity.ok(availableRooms);
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

    @PostMapping("/search")
    public ResponseEntity<List<RoomInfo>> searchAvailableRooms(@RequestBody RoomSearchRequest request) {
        List<RoomInfo> availableRooms = roomService.searchAvailableRooms(
                request.getCheckIn(),
                request.getCheckOut(),
                request.getRoomType()
        );
        return ResponseEntity.ok(availableRooms);
    }

    @PostMapping("/cleanup")
    public ResponseEntity<String> triggerCleanup(@RequestParam(required = false) String key) {
        if (key != null && !key.equals("YOUR_SECRET_KEY")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
        }

        reservationCleanupService.cancelUnpaidReservationsAfter12Hours();
        return ResponseEntity.ok("Reservation cleanup task executed.");
    }


}
