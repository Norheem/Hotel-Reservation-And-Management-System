package com.hotel.reservation.payload.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfo {

    private String paymentId;
    private String payerId;
    private String status;
    private String paymentMethod;
    private String totalAmount;
    private String currency;
    private String description;
    private String approvalLink;
}
