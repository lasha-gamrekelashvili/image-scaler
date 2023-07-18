package com.example.imagescaler.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpscaleRequest {
    private String execEndpoint;

    private List<DataItem> data;

    private Map<String, String> parameters;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataItem {
        private String blob;
    }
}
