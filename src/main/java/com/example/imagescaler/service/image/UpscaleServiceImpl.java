package com.example.imagescaler.service.image;

import com.example.imagescaler.exception.CustomImageProcessingException;
import com.example.imagescaler.request.UpscaleRequest;
import com.example.imagescaler.response.UpscaleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService {

    @Value("${api.url}")
    private String API_URL;
    @Value("${api.key}")
    public String API_KEY;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final String SCALE_SIZE = "1200:-1";

    @Override
    public Mono<byte[]> upscale(MultipartFile image) {
        try {
            validateImageSize(image);

            var base64Data = new String(image.getBytes());
            return validateBase64Data(base64Data).flatMap(data -> {
                var dataItem = new UpscaleRequest.DataItem(data);
                String jsonPayload;

                try {
                    jsonPayload = objectMapper.writeValueAsString(createUpscaleRequest(dataItem));
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException(e));
                }

                return webClientBuilder.baseUrl(API_URL)
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .defaultHeader(HttpHeaders.AUTHORIZATION, API_KEY)
                        .build()
                        .post()
                        .body(BodyInserters.fromValue(jsonPayload))
                        .retrieve()
                        .bodyToMono(String.class)
                        .flatMap(responseBody -> parseResponse(responseBody)
                                .map(response -> extractDataItem(response)
                                        .map(item -> Base64.getDecoder().decode(item.getBlob()))
                                        .orElseThrow(() -> new CustomImageProcessingException("Unexpected error on server side."))
                                )
                                .switchIfEmpty(Mono.error(new CustomImageProcessingException("No data found in the response.")))
                        );
            });
        } catch (Exception ex) {
            return Mono.error(new CustomImageProcessingException(ex.getMessage()));
        }
    }

    private Optional<UpscaleResponse.DataItem> extractDataItem(UpscaleResponse response) {
        return response.getData().stream().filter(Objects::nonNull).findFirst();
    }

    private void validateImageSize(MultipartFile image) throws CustomImageProcessingException.InvalidImageSizeException {
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new CustomImageProcessingException.InvalidImageSizeException("File size exceeds the maximum allowed limit of 10mb.");
        }
    }

    private Mono<String> validateBase64Data(String base64Data) {
        return Mono.fromCallable(() -> Base64.getDecoder().decode(base64Data))
                .map(data -> base64Data).onErrorResume(throwable -> Mono.error(()-> new CustomImageProcessingException.InvalidBase64DataException("Invalid base64 data.")));
    }

    private UpscaleRequest createUpscaleRequest(UpscaleRequest.DataItem dataItem) {
        return new UpscaleRequest("/upscale", Collections.singletonList(dataItem), createParametersMap());
    }

    private Map<String, String> createParametersMap() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("scale", SCALE_SIZE);
        return parameters;
    }

    private Mono<UpscaleResponse> parseResponse(String responseBody) {
        return Mono.fromCallable(() -> {
            var responseJson = objectMapper.readTree(responseBody);
            if (responseJson.has("header") && responseJson.get("header").has("status") && responseJson.get("header").get("status").has("exception")) {
                var exceptionNode = responseJson.get("header").get("status").get("exception");
                if (exceptionNode.has("args")) {
                    var argsNode = exceptionNode.get("args");
                    var errorMessage = argsNode.get(0).asText();
                    throw new CustomImageProcessingException.UpscaleApiException(errorMessage);
                } else {
                    throw new CustomImageProcessingException.UpscaleApiUnexpectedException("API Error: No exception information found in the response.");
                }
            }
            return objectMapper.readValue(responseBody, UpscaleResponse.class);
        });
    }
}
