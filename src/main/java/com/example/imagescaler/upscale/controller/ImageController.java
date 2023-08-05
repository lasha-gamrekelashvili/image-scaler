package com.example.imagescaler.upscale.controller;

import com.example.imagescaler.upscale.service.UpscaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
  private final UpscaleService upscaleService;

  @PostMapping(
    value = "/super-resolution",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.IMAGE_JPEG_VALUE
  )
  @Operation(
    summary = "Takes a multipart/form request with a base64 image data (resolution < 1024x1024) and scales it up (resolution >=1200x1200)"
  )
  @ApiResponses(
    value = {
      @ApiResponse(
        responseCode = "200",
        description = "Image scaled up successfully.",
        content = {
          @Content(
            mediaType = MediaType.IMAGE_JPEG_VALUE,
            schema = @Schema(type = "string", format = "binary")
          )
        }
      ),
      @ApiResponse(
        responseCode = "400",
        description = "Provided image was bigger than 1024x1024, file was corrupted or was not in the base64 format.",
        content = {
          @Content(
            mediaType = "text/plain",
            schema = @Schema(implementation = String.class)
          )
        }
      ),
      @ApiResponse(
        responseCode = "500",
        description = "Unexpected error on server side.",
        content = {
          @Content(
            mediaType = "text/plain",
            schema = @Schema(implementation = String.class)
          )
        }
      )
    }
  )
  public Mono<byte[]> uploadImage(@RequestPart("file") MultipartFile image) {
    return upscaleService.upscale(image);
  }
}
