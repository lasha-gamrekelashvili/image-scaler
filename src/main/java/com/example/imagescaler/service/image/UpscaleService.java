package com.example.imagescaler.service.image;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UpscaleService {
    ResponseEntity<String> upscale(MultipartFile image);
}
