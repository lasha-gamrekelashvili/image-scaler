package com.example.imagescaler;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class ImageScalerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ImageScalerApplication.class, args);
  }
}
