package com.hotel.reservation.payload.response;


import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.entity.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomInfo {

    private Long roomId;

    private String roomNumber;

    private RoomType roomType;

    private BigDecimal price;

    private RoomStatus roomStatus;

    private int roomCapacity;
}
