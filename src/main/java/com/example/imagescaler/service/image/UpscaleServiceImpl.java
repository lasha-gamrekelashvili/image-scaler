package com.example.imagescaler.service.image;

import com.example.imagescaler.request.UpscaleRequest;
import com.example.imagescaler.response.UpscaleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class UpscaleServiceImpl implements UpscaleService{

    @Value("${api.url}")
    private  String API_URL;
    @Value("${api.key}")
    public  String API_KEY;
    @Override
    public ResponseEntity<String> upscale(MultipartFile image) {
        try {
            var imageBlob =  Base64.getEncoder().encodeToString(image.getBytes());

            UpscaleRequest.DataItem dataItem = new UpscaleRequest.DataItem(imageBlob);
            UpscaleRequest requestPayload = new UpscaleRequest(
                    "/upscale",
                    Collections.singletonList(dataItem),
                    createParametersMap("scale", "1200:-1")
            );
            String jsonPayload = new ObjectMapper().writeValueAsString(requestPayload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", API_KEY);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);
            var response = new RestTemplate().exchange(API_URL, HttpMethod.POST, requestEntity, UpscaleResponse.class);
            var OptionalData = Objects.requireNonNull(response.getBody()).getData().stream().filter(Objects::nonNull).findFirst();

            return OptionalData.map(item -> new ResponseEntity<>(item.getBlob(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST));
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, String> createParametersMap(String key, String value) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(key, value);
        return parameters;
    }
}
