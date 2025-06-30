package com.hotel.reservation.entity;

import com.hotel.reservation.entity.enums.PaymentMethod;
import com.hotel.reservation.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "payment_tbl")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String paymentId;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    @Positive
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
