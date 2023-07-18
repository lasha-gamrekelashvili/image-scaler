package com.example.imagescaler.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpscaleRequest {
    @JsonProperty("execEndpoint")
    private String execEndpoint;

    @JsonProperty("data")
    private List<DataItem> data;

    @JsonProperty("parameters")
    private Map<String, String> parameters;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataItem {
        @JsonProperty("blob")
        private String blob;
    }
}
