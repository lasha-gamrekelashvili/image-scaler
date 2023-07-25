package com.example.imagescaler.provider.upscaleApi;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public interface UpscaleApiProvider {

    Mono<String> upscale(String jsonPayload);
}
