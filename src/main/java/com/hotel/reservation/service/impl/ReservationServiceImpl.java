package com.hotel.reservation.service.impl;

import com.hotel.reservation.entity.Payment;
import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.entity.Room;
import com.hotel.reservation.entity.User;
import com.hotel.reservation.entity.enums.PaymentMethod;
import com.hotel.reservation.entity.enums.PaymentStatus;
import com.hotel.reservation.entity.enums.ReservationStatus;
import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.exception.customExceptions.PaymentFailedException;
import com.hotel.reservation.exception.customExceptions.ResourceNotFoundException;
import com.hotel.reservation.exception.customExceptions.RoomNotFoundException;
import com.hotel.reservation.exception.customExceptions.RoomUnavailableException;
import com.hotel.reservation.payload.request.PayPalRequest;
import com.hotel.reservation.payload.request.ReservationRequest;
import com.hotel.reservation.payload.response.*;
import com.hotel.reservation.repository.PaymentRepository;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.repository.RoomRepository;
import com.hotel.reservation.repository.UserRepository;
import com.hotel.reservation.service.EmailService;
import com.hotel.reservation.service.PayPalService;
import com.hotel.reservation.service.ReservationService;
import com.hotel.reservation.utils.AccountUtils;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;


    private final EmailService emailService;

    private final PayPalService payPalService;


    @Transactional
    @Override
    public ReservationResponse createReservation(ReservationRequest request, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Room> availableRooms = roomRepository.findByRoomTypeAndRoomStatus(request.getRoomType(), RoomStatus.AVAILABLE);

        if (availableRooms.isEmpty()) {
            List<Room> alternativeRooms = roomRepository.findByRoomStatus(RoomStatus.AVAILABLE);

            if (alternativeRooms.isEmpty()) {
                throw new RoomNotFoundException("No available " + request.getRoomType() + " rooms at the moment.");
            }

            List<AlternativeRoomDto> alternatives = alternativeRooms.stream()
                    .map(room -> new AlternativeRoomDto(
                            room.getRoomNumber(),
                            room.getRoomType().name(), // assuming enum
                            room.getPrice()
                    ))
                    .collect(Collectors.toList());

            String message = "Requested room type '" + request.getRoomType() + "' is not available. Here are some alternatives:";


            StringBuilder suggestions = new StringBuilder("Requested room type '")
                    .append(request.getRoomType())
                    .append("' is not available. Here are some alternatives:\n\n");

            int count = 1;
            for (Room altRoom : alternativeRooms) {
                suggestions.append(count).append(". Room Number: ").append(altRoom.getRoomNumber()).append("\n")
                        .append(", Room Type: ").append(altRoom.getRoomType()).append("\n")
                        .append(", Price: ").append(altRoom.getPrice()).append("\n\n");
                count++;
            }

            throw new RoomUnavailableException(suggestions.toString());

        }

        Room room = availableRooms.get(0);


        if (!isRoomAvailable(request.getCheckIn(), request.getCheckOut(), room.getId())) {
            throw new RoomUnavailableException("Room is not available for selected dates.");
        }

        BigDecimal totalPrice = calculateTotalPrice(room, request.getCheckIn(), request.getCheckOut());

        PaymentMethod method = request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CASH;

        Reservation reservation = Reservation.builder()
                .reservationCode(UUID.randomUUID().toString())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .totalPrice(totalPrice)
                .reservationStatus(ReservationStatus.BOOKED)
                .user(user)
                .room(room)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        Payment payment = Payment.builder()
                .paymentDate(LocalDate.now())
                .amount(totalPrice)
                .paymentMethod(method)
                .paymentStatus(PaymentStatus.PENDING)
                .reservation(savedReservation)
                .build();

        Payment savedPayment = paymentRepository.save(payment);


        boolean isPaymentSuccessful = false;

        try {
            double formattedTotal = totalPrice.doubleValue();

            // Create the PayPal request object
            PayPalRequest payPalRequest = PayPalRequest.builder()
                    .total(formattedTotal)  // Use the calculated total
                    .currency("USD")
                    .method("paypal")
                    .intent("sale")
                    .description("Hotel Roomify Room Booking: " + room.getRoomNumber())
                    .cancelUrl("http://localhost:8080/api/v1/reservation/cancel")
                    .successUrl("http://localhost:8080/api/v1/reservation/success")
                    .build();

            PaymentResponse paymentResponse = payPalService.createPayment(payPalRequest, savedReservation.getId());

            isPaymentSuccessful = paymentResponse.getPaymentInfo().getApprovalLink() != null;
            if (isPaymentSuccessful) {
                System.out.println("Redirect to: " + paymentResponse.getPaymentInfo().getApprovalLink());
            }

        } catch (PayPalRESTException e) {
            e.printStackTrace();
            System.err.println("Payment failed: " + e.getMessage());
        }

        if (!isPaymentSuccessful) {
            savedPayment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(savedPayment);
            throw new PaymentFailedException("Payment failed, reservation not completed.");
        }

        savedPayment.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(savedPayment);

        savedReservation.setReservationStatus(ReservationStatus.BOOKED);
        reservationRepository.save(savedReservation);

        room.setRoomStatus(RoomStatus.BOOKED);
        roomRepository.save(room);

        return mapToReservationResponse(savedReservation);
    }

    @Override
    @Transactional
    public void checkInReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (reservation.getReservationStatus() != ReservationStatus.BOOKED) {
            throw new IllegalStateException("Reservation must be in BOOKED status to check in.");
        }

        reservation.setReservationStatus(ReservationStatus.OCCUPIED);
        reservationRepository.save(reservation);

        Room room = reservation.getRoom();
        room.setRoomStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);
    }


    @Override
    public boolean isRoomAvailable(LocalDate checkIn, LocalDate checkOut, Long roomId) {
        return reservationRepository.countByRoomIdAndDateRange(roomId, checkIn, checkOut) == 0;
    }

    @Transactional
    @Override
    public ReservationResponse updateReservation(Long reservationId, ReservationRequest request, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (reservation.getUser().getId() != userId) {
            throw new RuntimeException("User is not authorized to update this reservation");
        }

        Room currentRoom = reservation.getRoom();
        currentRoom.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(currentRoom);

        List<Room> availableRooms = roomRepository.findByRoomTypeAndRoomStatus(request.getRoomType(), RoomStatus.AVAILABLE);
        if (availableRooms.isEmpty()) {
            throw new RoomNotFoundException("No available room found for type: " + request.getRoomType());
        }

        Room newRoom = availableRooms.get(0);
        if (!isRoomAvailable(request.getCheckIn(), request.getCheckOut(), newRoom.getId())) {
            throw new RoomUnavailableException("Room is not available for selected dates.");
        }

        BigDecimal totalPrice = calculateTotalPrice(newRoom, request.getCheckIn(), request.getCheckOut());
        reservation.setCheckIn(request.getCheckIn());
        reservation.setCheckOut(request.getCheckOut());
        reservation.setTotalPrice(totalPrice);
        reservation.setRoom(newRoom);

        newRoom.setRoomStatus(RoomStatus.BOOKED);
        roomRepository.save(newRoom);

        Reservation updatedReservation = reservationRepository.save(reservation);
        return mapToReservationResponse(updatedReservation);
    }


    @Override
    public List<ReservationInfo> getUserReservations(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: ", userId));

        return reservationRepository.findByUserId(userId).stream()
                .map(reservation -> new ReservationInfo(
                        reservation.getId(),
                        reservation.getReservationCode(),
                        reservation.getCheckIn(),
                        reservation.getCheckOut(),
                        reservation.getTotalPrice(),
                        reservation.getReservationStatus(),
                        reservation.getUser().getId(),
                        reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName(),
                        reservation.getRoom().getId(),
                        reservation.getRoom().getRoomNumber(),
                        reservation.getRoom().getRoomType()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public String cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation has already been canceled.");
        }
        reservationRepository.delete(reservation);
        return "Reservation with ID " + reservation.getId() + " has been cancel successfully";

    }

    @Override
    @Transactional
    public void checkOutReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (reservation.getReservationStatus() != ReservationStatus.OCCUPIED) {
            throw new IllegalStateException("Reservation must be in OCCUPIED status to check out.");
        }

        reservation.setReservationStatus(ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        Room room = reservation.getRoom();
        room.setRoomStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        sendPaymentReceiptEmail(reservation);
    }

    private void sendPaymentReceiptEmail(Reservation reservation) {
        User user = reservation.getUser();


        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new RuntimeException("Payment not found for reservation"));


        String subject = "Your Roomify Payment Receipt";
        String message = String.format(
                "Dear %s,\n\n" +
                        "Thank you for staying with Roomify!\n\n" +
                        "Here is your payment receipt:\n" +
                        "Reservation Code: %s\n" +
                        "Check-in Date: %s\n" +
                        "Check-out Date: %s\n" +
                        "Room Type: %s\n" +
                        "Total Paid: $%.2f\n\n" +
                        "We hope to see you again soon!\n\n" +
                        "Best regards,\nRoomify Team",
                user.getFirstName(),
                reservation.getReservationCode(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                reservation.getRoom().getRoomType(),
                payment.getAmount()
        );

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject(subject)
                .messageBody(message)
                .build();

        emailService.sendEmailToken(emailDetails);
    }


    private BigDecimal calculateTotalPrice(Room room, LocalDate checkIn, LocalDate checkOut) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        return room.getPrice().multiply(BigDecimal.valueOf(days));
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .responseCode(AccountUtils.ROOM_RESERVATION_BOOKED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ROOM_RESERVATION_BOOKED_SUCCESS_MESSAGE)
                .reservationInfo(ReservationInfo.builder()
                        .reservationId(reservation.getId())
                        .reservationCode(reservation.getReservationCode())
                        .checkIn(reservation.getCheckIn())
                        .checkOut(reservation.getCheckOut())
                        .totalPrice(reservation.getTotalPrice())
                        .reservationStatus(reservation.getReservationStatus())
                        .customerId(reservation.getUser().getId())
                        .customerName(reservation.getUser().getFirstName())
                        .roomId(reservation.getRoom().getId())
                        .roomNumber(reservation.getRoom().getRoomNumber())
                        .roomType(reservation.getRoom().getRoomType())
                        .build())
                .build();
    }
}
