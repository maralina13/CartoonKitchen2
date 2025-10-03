package com.cartoonkitchen.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "media_files")
@Getter @Setter
public class MediaFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String originalFilename;
    @Column(nullable = false) private String storedFilename;
    @Column(nullable = false) private String contentType;
    @Column(nullable = false) private long size;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private MediaType type;

    @Column(nullable = false) private String storagePath;
    @Column(nullable = false) private Instant createdAt = Instant.now();
}
