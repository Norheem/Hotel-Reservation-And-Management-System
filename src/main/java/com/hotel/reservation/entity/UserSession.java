package com.hotel.reservation.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSession {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime loginTime;

    @Column
    private LocalDateTime logoutTime;

    @Column
    private Long duration;
}
