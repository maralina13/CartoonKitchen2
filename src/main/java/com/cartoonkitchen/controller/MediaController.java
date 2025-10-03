package com.cartoonkitchen.controller;

import com.cartoonkitchen.entity.MediaFile;
import com.cartoonkitchen.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> upload(@RequestPart("file") MultipartFile file) {
        MediaFile mf = mediaService.store(file);
        return ResponseEntity.ok(Map.of(
                "id", mf.getId(),
                "url", "/media/" + mf.getId(),
                "contentType", mf.getContentType(),
                "size", mf.getSize()
        ));
    }

    @GetMapping("/media/{id}")
    public ResponseEntity<Resource> serve(@PathVariable Long id) {
        MediaFile mf = null;
        Resource res = mediaService.loadAsResource(id);
        try {
            var repoField = mediaService.getClass().getDeclaredField("mediaRepo");
        } catch (Exception ignore) {}

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"file-"+id+"\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(res);
    }
}
