package com.example.imagescaler.upscale.request;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class UpscaleRequest {
  private final String execEndpoint;

  private final List<DataItem> data;

  private final Map<String, String> parameters;

  @Data
  public static class DataItem {
    private final String blob;
  }
}
