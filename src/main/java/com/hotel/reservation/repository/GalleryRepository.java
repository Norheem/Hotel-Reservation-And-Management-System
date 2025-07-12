package com.hotel.reservation.repository;

import com.hotel.reservation.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    Optional<Gallery> findFirstByIsBackgroundImageTrue();
}
