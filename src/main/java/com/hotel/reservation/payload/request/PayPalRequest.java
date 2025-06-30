package com.hotel.reservation.payload.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayPalRequest {
    Double total;
    String currency;
    String method;
    String intent;
    String description;
    String cancelUrl;
    String successUrl;
}
