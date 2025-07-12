package com.hotel.reservation.service;

import com.hotel.reservation.entity.Gallery;

import java.util.List;

public interface GalleryService {

    List<Gallery> getAllImages();
    Gallery getImageById(Long id);
    Gallery getBackgroundImage();
}
