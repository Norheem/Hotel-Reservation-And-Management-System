package com.hotel.reservation.controller;


import com.hotel.reservation.entity.enums.Gender;
import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.entity.enums.RoomType;
import com.hotel.reservation.payload.request.LoginRequest;
import com.hotel.reservation.payload.request.RegistrationRequest;
import com.hotel.reservation.payload.request.RoomRequest;
import com.hotel.reservation.payload.response.*;
import com.hotel.reservation.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final UserSessionService userSessionService;

    private final UserService userService;

    private final RoomService roomService;

    private final ReservationService reservationService;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register
            (@RequestBody RegistrationRequest request,
             @RequestParam Gender gender){
        return ResponseEntity.ok(adminService.register(request, gender));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAdmin(@RequestParam String token) {
        return ResponseEntity.ok(adminService.verifyAdmin(token));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(adminService.login(request));
    }

    @PostMapping("/add-room")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody @Valid RoomRequest roomRequest) {
        return ResponseEntity.ok(roomService.createRoom(roomRequest));
    }

    @PutMapping("/room/update/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId, @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(roomId, request));
    }

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RoomInfo>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/rooms/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RoomInfo>> getRoomsByStatus(@RequestParam RoomStatus roomStatus) {
        List<RoomInfo> rooms = roomService.getAllRoomsByStatus(roomStatus);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/type")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RoomInfo>> getRoomsByType(@RequestParam RoomType roomType) {
        List<RoomInfo> rooms = roomService.getAllRoomsByType(roomType);
        return ResponseEntity.ok(rooms);
    }


    @PostMapping("/customer/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AuthResponse> createCustomer(
            @RequestBody RegistrationRequest request,
            @RequestParam Gender gender) {
        return ResponseEntity.ok(userService.register(request, gender));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RegistrationInfo>> getAlUsers(@RequestParam(required = false) Long customerId,
                                                                  @RequestParam(required = false) String email) {
        return ResponseEntity.ok(adminService.getAllUsers(customerId, email));
    }


    @GetMapping("/reservation/availability")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Boolean> checkAvailability(@RequestParam LocalDate checkIn,
                                                     @RequestParam LocalDate checkOut,
                                                     @RequestParam Long roomId) {
        return ResponseEntity.ok(reservationService.isRoomAvailable(checkIn, checkOut, roomId));
    }

//    @PostMapping("/reservation")
//    @PreAuthorize("hasAuthority('ADMIN')")
//    public ResponseEntity<ReservationResponse> createReservation(@RequestBody @Valid ReservationRequest request) {
//        return ResponseEntity.ok(reservationService.createReservation(request));
//    }


    @DeleteMapping("/room/{roomId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok("Room deleted successfully.");
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(adminService.logout(authHeader));
    }
}
