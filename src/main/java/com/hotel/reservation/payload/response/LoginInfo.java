package com.hotel.reservation.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginInfo {

    private String firstName;

    private String email;

    private String token;
}
