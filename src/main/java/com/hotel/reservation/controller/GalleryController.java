package com.hotel.reservation.controller;

import com.hotel.reservation.entity.Gallery;
import com.hotel.reservation.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    public List<Gallery> getAllImages() {
        return galleryService.getAllImages();
    }

    @GetMapping("/{id}")
    public Gallery getImageById(@PathVariable Long id) {
        return galleryService.getImageById(id);
    }

    @GetMapping("/background")
    public ResponseEntity<Gallery> getBackgroundImage() {
        return ResponseEntity.ok(
                galleryService.getBackgroundImage()
        );
    }

}
