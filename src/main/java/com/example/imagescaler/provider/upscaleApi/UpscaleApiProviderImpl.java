package com.example.imagescaler.provider.upscaleApi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpscaleApiProviderImpl implements UpscaleApiProvider{

    @Value("${api.url}")
    private String API_URL;
    @Value("${api.key}")
    public String API_KEY;
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<String>upscale(String jsonPayload) {
        return webClientBuilder.baseUrl(API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, API_KEY)
                .build()
                .post()
                .body(BodyInserters.fromValue(jsonPayload))
                .retrieve()
                .bodyToMono(String.class);
    }
}
