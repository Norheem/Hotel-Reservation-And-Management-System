package com.hotel.reservation.service.impl;

import com.hotel.reservation.entity.Reservation;
import com.hotel.reservation.entity.Room;
import com.hotel.reservation.payload.request.PayPalRequest;
import com.hotel.reservation.payload.response.PaymentInfo;
import com.hotel.reservation.payload.response.PaymentResponse;
import com.hotel.reservation.repository.ReservationRepository;
import com.hotel.reservation.service.PayPalService;
import com.hotel.reservation.utils.AccountUtils;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

    private final APIContext apiContext;

    private final ReservationRepository reservationRepository;

    @Override
    public PaymentResponse createPayment(PayPalRequest payPalRequest, Long reservationId){
        try {

            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + reservationId));

            Room room = reservation.getRoom();

            long days = ChronoUnit.DAYS.between(reservation.getCheckIn(), reservation.getCheckOut());
            BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(days));

            String formattedTotal = String.format(Locale.forLanguageTag(payPalRequest.getCurrency()), "%.2f", totalPrice);


            Amount amount = new Amount();
            amount.setCurrency(payPalRequest.getCurrency());
            amount.setTotal(formattedTotal);

            Transaction transaction = new Transaction();
            transaction.setDescription("Hotel Roomify Room Booking: " + room.getRoomNumber());
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod(payPalRequest.getMethod());

            Payment payment = new Payment();
//            payment.setIntent(payPalRequest.getIntent());
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(payPalRequest.getCancelUrl());
            redirectUrls.setReturnUrl(payPalRequest.getSuccessUrl());


            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);

            PaymentResponse paymentResponse = PaymentResponse.builder()
                    .responseCode(AccountUtils.CREATE_PAYMENT_SUCCESSFUL_CODE)
                    .responseMessage(AccountUtils.CREATE_PAYMENT_SUCCESSFUL_MESSAGE)
                    .paymentInfo(PaymentInfo.builder()
                            .paymentId(createdPayment.getId())
                            .status(createdPayment.getState())
                            .totalAmount(createdPayment.getTransactions().get(0).getAmount().getTotal())
                            .currency(createdPayment.getTransactions().get(0).getAmount().getCurrency())
                            .description(createdPayment.getTransactions().get(0).getDescription())
                            .approvalLink(createdPayment.getLinks().stream()
                                    .filter(link -> link.getRel().equals("approval_url"))
                                    .findFirst()
                                    .map(link -> link.getHref())
                                    .orElse("No approval URL found"))
                            .build())
                    .build();

            return paymentResponse;

            //return payment.create(apiContext);
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Error occurred while creating PayPal payment: " + e.getMessage(), e);
        }
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);


        return payment.execute(apiContext, paymentExecution);
    }

    public String getUserEmailFromPayment(Payment payment) {

        if (payment.getPayer() != null && payment.getPayer().getPayerInfo() != null) {
            return payment.getPayer().getPayerInfo().getEmail();
        }

        return "no-email@roomify.com";
    }

}
