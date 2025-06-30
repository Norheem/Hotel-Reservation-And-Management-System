package com.hotel.reservation.payload.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String password;
}
