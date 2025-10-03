package com.cartoonkitchen.service;

import com.cartoonkitchen.entity.MediaFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    MediaFile store(MultipartFile file);
    Resource loadAsResource(Long id);
}
