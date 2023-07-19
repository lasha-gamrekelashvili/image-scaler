package com.example.imagescaler.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

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
