package com.hotel.reservation.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponseWrapper {

    private String responseMessage;
    private ReservationInfo reservationInfo;
    private List<AlternativeRoomDto> alternativeRooms;
    private boolean success;
}
