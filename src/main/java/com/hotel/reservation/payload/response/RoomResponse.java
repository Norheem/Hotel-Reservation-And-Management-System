package com.hotel.reservation.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {

    private String responseCode;

    private String responseMessage;

    private RoomInfo roomInfo;
}
