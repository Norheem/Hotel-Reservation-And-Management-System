package com.hotel.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "token_tbl")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class TokenVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    private boolean revoked;

    private LocalDateTime expirationTime;

    @OneToOne
    @JoinColumn(name = "user_tbl", nullable = false)
    private User user;
}
