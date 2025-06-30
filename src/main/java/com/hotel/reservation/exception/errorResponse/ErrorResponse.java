package com.hotel.reservation.exception.errorResponse;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResponse {

    private String error;
    private String message;
}
