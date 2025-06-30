package com.hotel.reservation.service;

import com.hotel.reservation.payload.request.PayPalRequest;
import com.hotel.reservation.payload.response.PaymentResponse;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Service;

@Service
public interface PayPalService {

    PaymentResponse createPayment(PayPalRequest payPalRequest, Long reservationId) throws PayPalRESTException;

    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

    String getUserEmailFromPayment(Payment payment);
}
