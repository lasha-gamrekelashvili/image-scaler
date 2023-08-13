package com.example.imagescaler.upscale.exception.handler;

import com.example.imagescaler.upscale.exception.CustomImageProcessingException;
import java.net.URI;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(
    { CustomImageProcessingException.InvalidBase64DataException.class }
  )
  public ProblemDetail handleInvalidBase64Exception(
    CustomImageProcessingException ex
  ) {
    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      ex.getMessage()
    );
    problemDetail.setType(URI.create(("problems/invalid-64")));
    return problemDetail;
  }

  @ExceptionHandler(
    { CustomImageProcessingException.InvalidImageSizeException.class }
  )
  public ProblemDetail handleInvalidImageSizeException(
    CustomImageProcessingException ex
  ) {
    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      ex.getMessage()
    );
    problemDetail.setType(URI.create(("problems/invalid-size")));
    return problemDetail;
  }

  @ExceptionHandler(
    { CustomImageProcessingException.UpscaleApiException.class }
  )
  public ProblemDetail handleUpscaleApiException(
    CustomImageProcessingException ex
  ) {
    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      ex.getMessage()
    );
    problemDetail.setType(URI.create(("problems/upscale-api-error")));
    return problemDetail;
  }

  @ExceptionHandler(
    { CustomImageProcessingException.UpscaleApiUnexpectedException.class }
  )
  public ProblemDetail handleUpscaleApiUnexpectedException(
    CustomImageProcessingException ex
  ) {
    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      ex.getMessage()
    );
    problemDetail.setType(
      URI.create(("problems/upscale-api-unexpected-error"))
    );
    return problemDetail;
  }

  @ExceptionHandler(CustomImageProcessingException.class)
  public ProblemDetail handleGenericImageProcessingException(Exception ex) {
    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ex.getMessage()
    );
    problemDetail.setType(
      URI.create(("problems/generic-image-processing-error"))
    );
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGenericException(Exception ex) {
    val problemDetail = ProblemDetail.forStatusAndDetail(
      HttpStatus.INTERNAL_SERVER_ERROR,
      ex.getMessage()
    );
    problemDetail.setType(
      URI.create(("problems/unexpected-image-processing-error"))
    );
    return problemDetail;
  }
}
