package com.example.imagescaler.service.image;

import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface UpscaleService {
    Mono<byte[]> upscale(MultipartFile image);
}
