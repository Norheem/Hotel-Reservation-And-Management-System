package com.hotel.reservation.service.impl;

import com.hotel.reservation.entity.Gallery;
import com.hotel.reservation.repository.GalleryRepository;
import com.hotel.reservation.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private final GalleryRepository galleryRepository;

    @Override
    public List<Gallery> getAllImages() {
        return galleryRepository.findAll();
    }

    @Override
    public Gallery getImageById(Long id) {
        return galleryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + id));
    }

    @Override
    public Gallery getBackgroundImage() {
        return galleryRepository.findFirstByIsBackgroundImageTrue()
                .orElseThrow(() -> new RuntimeException("No background image found"));
    }
}
