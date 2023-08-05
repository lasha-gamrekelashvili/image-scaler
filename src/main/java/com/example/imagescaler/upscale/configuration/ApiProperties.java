package com.example.imagescaler.upscale.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
@Getter
@Setter
public class ApiProperties {
  public String url;
  public String key;
}
