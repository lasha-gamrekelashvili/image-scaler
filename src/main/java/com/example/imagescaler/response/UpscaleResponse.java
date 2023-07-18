package com.example.imagescaler.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpscaleResponse {

    private Header header;

    private Map<String, String> parameters;

    private List<Route> routes;

    private List<DataItem> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private String requestId;

        private String status;

        private String execEndpoint;

        private String targetExecutor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Route {
        private String executor;

        private String startTime;

        private String endTime;

        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataItem {
        private String id;

        @JsonProperty("parent_id")
        private String parentId;

        private String granularity;

        private String adjacency;

        private String blob;

        private String tensor;

        @JsonProperty("mime_type")
        private String mimeType;

        private String text;

        private int weight;

        private String uri;

        private Map<String, String> tags;

        private String offset;

        private String location;

        private String embedding;

        private String modality;

        private String evaluations;

        private String scores;

        private String chunks;

        private String matches;
    }
}

