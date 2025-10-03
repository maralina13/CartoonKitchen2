package com.cartoonkitchen.service.impl;

import com.cartoonkitchen.entity.MediaFile;
import com.cartoonkitchen.entity.MediaType;
import com.cartoonkitchen.repository.MediaFileRepository;
import com.cartoonkitchen.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaFileRepository mediaRepo;

    private final Path root = Paths.get("uploads");

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg",    // jpeg
            "video/mp4",     // mp4
            "audio/mpeg"     // mp3
    );

    @Override
    public MediaFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new RuntimeException("Файл пуст");
        String ct = file.getContentType();
        if (ct == null || !ALLOWED.contains(ct)) {
            throw new RuntimeException("Недопустимый тип файла: " + ct);
        }
        try {
            Files.createDirectories(root);
            String ext = guessExt(ct);
            String stored = UUID.randomUUID() + ext;
            Path target = root.resolve(stored).normalize();
            // простая защита от path traversal
            if (!target.startsWith(root)) throw new RuntimeException("Некорректный путь");
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            MediaFile mf = new MediaFile();
            mf.setOriginalFilename(StringUtils.cleanPath(file.getOriginalFilename()==null ? stored : file.getOriginalFilename()));
            mf.setStoredFilename(stored);
            mf.setContentType(ct);
            mf.setSize(file.getSize());
            mf.setStoragePath(root.resolve(stored).toString());
            mf.setType(mapType(ct));
            mf = mediaRepo.save(mf);

            log.info("Файл сохранён: id={} name={}", mf.getId(), mf.getStoredFilename());
            return mf;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка сохранения файла", e);
        }
    }

    @Override
    public Resource loadAsResource(Long id) {
        MediaFile mf = mediaRepo.findById(id).orElseThrow(() -> new RuntimeException("Файл не найден"));
        Path p = Paths.get(mf.getStoragePath());
        if (!Files.exists(p)) throw new RuntimeException("Файл отсутствует на диске");
        return new FileSystemResource(p);
    }

    private static String guessExt(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "video/mp4"  -> ".mp4";
            case "audio/mpeg" -> ".mp3";
            default -> "";
        };
    }

    private static MediaType mapType(String contentType) {
        if (contentType.startsWith("image/")) return MediaType.IMAGE;
        if (contentType.startsWith("video/")) return MediaType.VIDEO;
        if (contentType.startsWith("audio/")) return MediaType.AUDIO;
        throw new IllegalArgumentException("Неизвестный тип: " + contentType);
    }
}
