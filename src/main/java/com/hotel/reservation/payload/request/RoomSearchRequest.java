package com.hotel.reservation.payload.request;

import com.hotel.reservation.entity.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomSearchRequest {

    private LocalDate checkIn;
    private LocalDate checkOut;
    private RoomType roomType;
}
