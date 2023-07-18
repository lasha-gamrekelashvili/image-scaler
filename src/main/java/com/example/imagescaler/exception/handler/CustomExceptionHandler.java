package com.example.imagescaler.exception.handler;

import com.example.imagescaler.exception.CustomImageProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class CustomExceptionHandler {


    @ExceptionHandler({CustomImageProcessingException.InvalidImageSizeException.class,
            CustomImageProcessingException.UpscaleApiException.class,
            CustomImageProcessingException.UpscaleApiUnexpectedException.class,
            CustomImageProcessingException.InvalidBase64DataException.class})
    public ResponseEntity<String> handleCustomImageProcessingException(CustomImageProcessingException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CustomImageProcessingException.class)
    public ResponseEntity<String> handleGenericImageProcessingException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
