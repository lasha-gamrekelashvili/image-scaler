package com.example.imagescaler.service.image;

import com.example.imagescaler.exception.CustomImageProcessingException;
import com.example.imagescaler.request.UpscaleRequest;
import com.example.imagescaler.response.UpscaleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService{

    @Value("${api.url}")
    private  String API_URL;
    @Value("${api.key}")
    public  String API_KEY;
    private final RestTemplate restTemplate;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final String SCALE_SIZE = "1200:-1";
    @Override
    public ResponseEntity<?> upscale(MultipartFile image) {
        try {
            validateImageSize(image);

            var base64Data = new String(image.getBytes());
            validateBase64Data(base64Data);

            var dataItem = new UpscaleRequest.DataItem(base64Data);
            var jsonPayload = new ObjectMapper().writeValueAsString(createUpscaleRequest(dataItem));

            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", API_KEY);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            var response = parseResponse(restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class));
            var OptionalData = Objects.requireNonNull(response.getData().stream().filter(Objects::nonNull).findFirst());

            return OptionalData.map(item -> {
                byte[] decodedImageBytes = Base64.getDecoder().decode(item.getBlob());
                return ResponseEntity.ok(new String(decodedImageBytes, StandardCharsets.UTF_8));
            }).orElse(ResponseEntity.badRequest().body("Failed to process the image."));

        } catch (IOException | CustomImageProcessingException.InvalidImageSizeException | CustomImageProcessingException.InvalidBase64DataException | CustomImageProcessingException.UpscaleApiException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    private void validateImageSize(MultipartFile image) throws CustomImageProcessingException.InvalidImageSizeException {
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new CustomImageProcessingException.InvalidImageSizeException("File size exceeds the maximum allowed limit of 10mb.");
        }
    }

    private void validateBase64Data(String base64Data) throws CustomImageProcessingException.InvalidBase64DataException {
        try {
            Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            throw new CustomImageProcessingException.InvalidBase64DataException("Invalid base64 encoded data.");
        }
    }

    private UpscaleRequest createUpscaleRequest(UpscaleRequest.DataItem dataItem) {
        return new UpscaleRequest("/upscale", Collections.singletonList(dataItem), createParametersMap());
    }

    private Map<String, String> createParametersMap() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("scale", SCALE_SIZE);
        return parameters;
    }

    private UpscaleResponse parseResponse(ResponseEntity<String> rawResponse) throws IOException, CustomImageProcessingException.UpscaleApiException {
        var responseBody = rawResponse.getBody();
        var objectMapper = new ObjectMapper();

        var responseJson = objectMapper.readTree(responseBody);

        if (responseJson.has("header") && responseJson.get("header").has("status") && responseJson.get("header").get("status").has("exception")) {
            var exceptionNode = responseJson.get("header").get("status").get("exception");
            if (exceptionNode.has("args")) {
                var argsNode = exceptionNode.get("args");
                var errorMessage = argsNode.get(0).asText();
                throw new CustomImageProcessingException.UpscaleApiException(errorMessage);
            } else {
                throw new CustomImageProcessingException.UpscaleApiException("API Error: No exception information found in the response.");
            }
        }
        return objectMapper.readValue(responseBody, UpscaleResponse.class);
    }
}
