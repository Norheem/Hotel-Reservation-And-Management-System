package com.hotel.reservation.auth.config;


import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

@Configuration
public class WebPushConfig implements WebMvcConfigurer {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public PushService pushService() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PushService pushService = new PushService();
        pushService.setPublicKey("BHIv_etyAq2MbJey4ByPK9LWgP5Po1I6ywGxfXt2KNSmRdCOOn3t0O5haUbmY9rNAzBMXyr3YV6Yz-unx5e8e5M");
        pushService.setPrivateKey("rc62e8j30QseF9ZAZl3E72p70_8zwD7aVr18AZCvB4E");
        pushService.setSubject("mailto:nohimsulaiman@gmail.com");
        return pushService;
        /**
         * To generate the keys here both public and private
         * If you have Node.js installed, run:
         *          npm install -g web-push
         *              after than run:
         *          web-push generate-vapid-keys
         *  You key will be generated and display on the terminal copy it and paste it herea
         */
    }
}
