package com.hotel.reservation.payload.request;


import com.hotel.reservation.entity.enums.PaymentMethod;
import com.hotel.reservation.entity.enums.ReservationStatus;
import com.hotel.reservation.entity.enums.RoomType;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ReservationRequest {

    private Long customerId;

    private String customerName;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private Long roomId;

    private RoomType roomType;

    private ReservationStatus reservationStatus;

    private PaymentMethod paymentMethod = PaymentMethod.CASH;
}
