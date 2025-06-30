package com.hotel.reservation.controller;


import com.hotel.reservation.payload.request.PayPalRequest;
import com.hotel.reservation.payload.response.EmailDetails;
import com.hotel.reservation.payload.response.PaymentResponse;
import com.hotel.reservation.service.EmailService;
import com.hotel.reservation.service.PayPalService;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PayPalController {

    private final PayPalService payPalService;

    private final EmailService emailService;


    @PostMapping("/create/{reservationId}")
    public RedirectView createPayment(@PathVariable Long reservationId, @RequestBody PayPalRequest payPalRequest) {
        try {
            String cancelUrl = "http://localhost:8080/api/v1/reservation/cancel";
            String successUrl = "http://localhost:8080/api/v1/reservation/success";
            payPalRequest.setCancelUrl(cancelUrl);
            payPalRequest.setSuccessUrl(successUrl);

            PaymentResponse paymentResponse = payPalService.createPayment(payPalRequest, reservationId);
            String approvalLink = paymentResponse.getPaymentInfo().getApprovalLink();

            if (approvalLink == null || approvalLink.isEmpty()) {
                log.error("Approval link not found.");
                return new RedirectView("/error");
            }

            return new RedirectView(approvalLink);
        } catch (PayPalRESTException e) {
            log.error("Error occurred while creating PayPal payment:", e);
            return new RedirectView("/error");
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> successPayment(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId) {
        try {
            // Execute the payment
            Payment payment = payPalService.executePayment(paymentId, payerId);

            if ("approved".equalsIgnoreCase(payment.getState())) {
                // Get payment summary details
                String amount = payment.getTransactions().get(0).getAmount().getTotal();
                String currency = payment.getTransactions().get(0).getAmount().getCurrency();
                String description = payment.getTransactions().get(0).getDescription();
                String transactionId = payment.getId();
//                String userEmail = getUserEmailFromPayment(payment);  // Implement this method
                String userEmail = payPalService.getUserEmailFromPayment(payment);

                // Prepare email message body
                String paymentSummary = String.format(
                        "Dear Customer,\n\n" +
                                "Your payment was successful!\n" +
                                "Transaction ID: %s\n" +
                                "Amount Paid: %s %s\n" +
                                "Description: %s\n\n" +
                                "Thank you for using Roomify.\n" +
                                "If you have any questions, contact us at support@roomify.com.\n\n" +
                                "Best regards,\n" +
                                "Roomify Team",
                        transactionId, amount, currency, description
                );

                // Send the payment confirmation email
                EmailDetails paymentEmail = EmailDetails.builder()
                        .recipient(userEmail)
                        .subject("Payment Confirmation")
                        .messageBody(paymentSummary)
                        .build();
                emailService.sendEmailToken(paymentEmail);

                return ResponseEntity.ok(paymentSummary);
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment not approved");
    }


    @GetMapping("/cancel")
    public String cancelPayment(@RequestParam(name = "error", required = false) String error) {
        if (error != null) {
            log.error("Payment canceled due to error: " + error);
        } else {
            log.info("Payment was canceled by the user.");
        }

        // Redirect to a custom cancel page where the user can take next steps
        return "payment-cancel";  // Render the cancel page
    }


}
