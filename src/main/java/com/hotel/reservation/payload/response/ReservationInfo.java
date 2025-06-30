package com.hotel.reservation.payload.response;


import com.hotel.reservation.entity.enums.ReservationStatus;
import com.hotel.reservation.entity.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationInfo {

    private Long reservationId;

    private String reservationCode;

    private LocalDate checkIn;

    private LocalDate checkOut;

    private BigDecimal totalPrice;

    private ReservationStatus reservationStatus;

    private Long customerId;

    private String customerName;

    private Long roomId;

    private String roomNumber;

    private RoomType roomType;
}
