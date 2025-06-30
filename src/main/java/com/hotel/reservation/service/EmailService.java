package com.hotel.reservation.service;

import com.hotel.reservation.payload.response.EmailDetails;
import org.springframework.stereotype.Service;


@Service
public interface EmailService {

    void sendEmailToken(EmailDetails emailDetails);
}
