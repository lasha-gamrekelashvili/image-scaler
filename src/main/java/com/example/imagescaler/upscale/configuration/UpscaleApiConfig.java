package com.example.imagescaler.upscale.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(UpscaleApiConfig.Configuration.class)
public class UpscaleApiConfig {

  @ConfigurationProperties(prefix = "upscale-api")
  @Data
  public static class Configuration {

    private String url;
    private String key;
  }
}
