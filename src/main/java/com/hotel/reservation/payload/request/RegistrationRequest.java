package com.hotel.reservation.payload.request;


import com.hotel.reservation.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private Gender gender;

    private String password;
}
