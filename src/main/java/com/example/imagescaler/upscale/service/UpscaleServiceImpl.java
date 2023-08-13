package com.example.imagescaler.upscale.service;

import com.example.imagescaler.upscale.apiprovider.UpscaleApiProvider;
import com.example.imagescaler.upscale.exception.CustomImageProcessingException;
import com.example.imagescaler.upscale.request.UpscaleRequest;
import com.example.imagescaler.upscale.response.UpscaleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpscaleServiceImpl implements UpscaleService {

  private final ObjectMapper objectMapper;
  private final UpscaleApiProvider upscaleApiProvider;
  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
  public static final String SCALE_SIZE = "1200:-1";

  @Override
  public Mono<byte[]> upscale(MultipartFile image) {
    return validateImageAndExtractBytes(image)
      .flatMap(this::validateBase64Data)
      .flatMap(this::createAndPostUpscaleRequest)
      .onErrorMap(
        CustomImageProcessingException.class,
        ex -> {
          if (
            ex instanceof CustomImageProcessingException.UpscaleApiException ||
            ex instanceof CustomImageProcessingException.UpscaleApiUnexpectedException ||
            ex instanceof CustomImageProcessingException.InvalidImageSizeException ||
            ex instanceof CustomImageProcessingException.InvalidBase64DataException
          ) {
            return ex;
          } else {
            return new CustomImageProcessingException(ex.getMessage());
          }
        }
      );
  }

  private Mono<byte[]> validateImageAndExtractBytes(MultipartFile image) {
    return Mono
      .fromCallable(() -> {
        if (image.getSize() > MAX_FILE_SIZE) {
          throw new CustomImageProcessingException.InvalidImageSizeException(
            "File size exceeds the maximum allowed limit of 10mb."
          );
        }
        return image.getBytes();
      })
      .onErrorMap(
        CustomImageProcessingException.InvalidImageSizeException.class,
        ex ->
          new CustomImageProcessingException.InvalidImageSizeException(
            ex.getMessage()
          )
      );
  }

  private Mono<String> validateBase64Data(byte[] bytes) {
    String base64Data = new String(bytes);
    return Mono
      .fromCallable(() -> Base64.getDecoder().decode(base64Data))
      .map(data -> base64Data)
      .onErrorResume(throwable ->
        Mono.error(() ->
          new CustomImageProcessingException.InvalidBase64DataException(
            "Invalid base64 data."
          )
        )
      );
  }

  private Mono<byte[]> createAndPostUpscaleRequest(String base64Data) {
    UpscaleRequest.DataItem dataItem = new UpscaleRequest.DataItem(base64Data);
    return Mono
      .fromCallable(() ->
        objectMapper.writeValueAsString(createUpscaleRequest(dataItem))
      )
      .flatMap(jsonPayload ->
        postToApi(jsonPayload)
          .onErrorResume(CustomImageProcessingException.class, Mono::error)
      )
      .onErrorMap(JsonProcessingException.class, RuntimeException::new);
  }

  private Mono<byte[]> postToApi(String jsonPayload) {
    return upscaleApiProvider
      .upscale(jsonPayload)
      .flatMap(this::parseResponse)
      .flatMap(this::extractAndDecodeDataItem)
      .onErrorResume(CustomImageProcessingException.class, Mono::error)
      .switchIfEmpty(
        Mono.error(
          new CustomImageProcessingException.UpscaleApiUnexpectedException(
            "Response was empty."
          )
        )
      );
  }

  private Mono<UpscaleResponse> parseResponse(String responseBody) {
    return Mono
      .fromCallable(() -> objectMapper.readTree(responseBody))
      .flatMap(responseJson -> {
        if (
          responseJson.has("header") &&
          responseJson.get("header").has("status") &&
          responseJson.get("header").get("status").has("exception")
        ) {
          var exceptionNode = responseJson
            .get("header")
            .get("status")
            .get("exception");
          if (exceptionNode.has("args")) {
            var argsNode = exceptionNode.get("args");
            var errorMessage = argsNode.get(0).asText();
            return Mono.error(
              new CustomImageProcessingException.UpscaleApiException(
                errorMessage
              )
            );
          } else {
            return Mono.error(
              new CustomImageProcessingException.UpscaleApiUnexpectedException(
                "API Error: No exception information found in the response."
              )
            );
          }
        }
        try {
          UpscaleResponse response = objectMapper.readValue(
            responseBody,
            UpscaleResponse.class
          );
          return Mono.just(response);
        } catch (IOException e) {
          return Mono.error(e);
        }
      })
      .onErrorResume(throwable -> {
        if (
          throwable instanceof CustomImageProcessingException.UpscaleApiException ||
          throwable instanceof CustomImageProcessingException.UpscaleApiUnexpectedException
        ) {
          return Mono.error(throwable);
        }
        return Mono.error(
          new CustomImageProcessingException.UpscaleApiUnexpectedException(
            "Unexpected error occurred."
          )
        );
      });
  }

  private Mono<byte[]> extractAndDecodeDataItem(UpscaleResponse response) {
    return extractDataItem(response)
      .map(item -> Base64.getDecoder().decode(item.getBlob()))
      .map(Mono::just)
      .orElseGet(() ->
        Mono.error(
          new CustomImageProcessingException.UpscaleApiException(
            "No data found in the response."
          )
        )
      );
  }

  private Optional<UpscaleResponse.DataItem> extractDataItem(
    UpscaleResponse response
  ) {
    return response.getData().stream().filter(Objects::nonNull).findFirst();
  }

  private UpscaleRequest createUpscaleRequest(
    UpscaleRequest.DataItem dataItem
  ) {
    return new UpscaleRequest(
      "/upscale",
      Collections.singletonList(dataItem),
      createParametersMap()
    );
  }

  private Map<String, String> createParametersMap() {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("scale", SCALE_SIZE);
    return parameters;
  }
}
