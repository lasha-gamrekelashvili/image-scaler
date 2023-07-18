package com.example.imagescaler.controller;

import com.example.imagescaler.service.image.UpscaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final UpscaleService upscaleService;

    @PostMapping(value = "/super-resolution", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Takes a multipart/form request with a base64 image data (resolution < 1024x1024) and scales it up (resolution >=1200x1200)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image scaled up successfully.",
                    content = { @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Provided image was bigger than 1024x1024, file was corrupted or was not in the base64 format.",
                    content = { @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "500", description = "Unexpected error on server side.",
                    content = { @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class)) }),
    })
    public ResponseEntity<?> uploadImage(@RequestPart("file") MultipartFile image) {
        byte[] scaledImage = upscaleService.upscale(image);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(scaledImage, headers, HttpStatus.OK);
    }
}
