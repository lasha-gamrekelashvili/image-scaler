package com.example.imagescaler.service.image;

import org.springframework.web.multipart.MultipartFile;

public interface UpscaleService {
    byte[] upscale(MultipartFile image);
}
