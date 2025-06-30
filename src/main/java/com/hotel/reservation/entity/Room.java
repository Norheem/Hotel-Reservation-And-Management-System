package com.hotel.reservation.entity;


import com.hotel.reservation.entity.enums.RoomStatus;
import com.hotel.reservation.entity.enums.RoomType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table (name = "room_tbl")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
public class Room extends BaseClass{

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(nullable = false)
    @Positive
    private BigDecimal price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @Column(nullable = false)
    private int roomCapacity;
}
