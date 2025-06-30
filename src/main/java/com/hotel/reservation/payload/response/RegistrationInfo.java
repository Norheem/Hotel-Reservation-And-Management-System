package com.hotel.reservation.payload.response;

import com.hotel.reservation.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationInfo {
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Role role;
}
