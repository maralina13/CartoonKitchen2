package com.cartoonkitchen.repository;

import com.cartoonkitchen.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long> { }
