package com.hotel.reservation.controller;

import com.hotel.reservation.entity.Gallery;
import com.hotel.reservation.repository.GalleryRepository;
import com.hotel.reservation.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;
    private final GalleryRepository galleryRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isBackgroundImage", defaultValue = "false") boolean isBackgroundImage
    ) {
        try {
            String url = cloudinaryService.uploadFile(file);

            // If new image is to be background, unset the old one
            if (isBackgroundImage) {
                galleryRepository.findAll().forEach(image -> {
                    if (image.isBackgroundImage()) {
                        image.setBackgroundImage(false);
                        galleryRepository.save(image);
                    }
                });
            }

            Gallery gallery = new Gallery();
            gallery.setImageUrl(url);
            gallery.setImageName(file.getOriginalFilename());
            gallery.setBackgroundImage(isBackgroundImage);
            galleryRepository.save(gallery);

            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
        }
    }
}
