package com.example.imagescaler.exception;

public class CustomImageProcessingException extends RuntimeException{
    public CustomImageProcessingException(String message) {
        super(message);
    }

    public static class InvalidImageSizeException extends CustomImageProcessingException {
        public InvalidImageSizeException(String message) {
            super(message);
        }
    }

    public static class InvalidBase64DataException extends CustomImageProcessingException {
        public InvalidBase64DataException(String message) {
            super(message);
        }
    }

    public static class UpscaleApiException extends CustomImageProcessingException {
        public UpscaleApiException(String message) {
            super(message);
        }
    }
}
