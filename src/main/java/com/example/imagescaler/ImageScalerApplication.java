package com.example.imagescaler;

import com.example.imagescaler.upscale.configuration.ApiProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@OpenAPIDefinition
@EnableConfigurationProperties(ApiProperties.class)
public class ImageScalerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ImageScalerApplication.class, args);
  }
}
