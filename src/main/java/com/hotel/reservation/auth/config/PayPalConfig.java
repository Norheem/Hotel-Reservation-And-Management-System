package com.hotel.reservation.auth.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public APIContext apiContext() {
        System.out.println("Client ID: " + clientId);
        System.out.println("Client Secret: " + clientSecret);
        System.out.println("Mode: " + mode);
        return new APIContext(clientId, clientSecret, mode);
    }
}



