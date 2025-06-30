package com.hotel.reservation.payload.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlternativeRoomDto {

    private String roomNumber;
    private String roomType;
    private BigDecimal price;
}
