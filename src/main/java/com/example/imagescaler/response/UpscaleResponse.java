package com.example.imagescaler.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpscaleResponse {

    private final Header header;

    private final Map<String, String> parameters;

    private final List<Route> routes;

    private final List<DataItem> data;

    @Data
    public static class Header {
        private final String requestId;

        private final String status;

        private final String execEndpoint;

        private final String targetExecutor;
    }

    @Data
    public static class Route {
        private final String executor;

        private final String startTime;

        private final String endTime;

        private final String status;
    }

    @Data
    public static class DataItem {
        private final String id;

        @JsonProperty("parent_id")
        private final String parentId;

        private final String granularity;

        private final String adjacency;

        private final String blob;

        private final String tensor;

        @JsonProperty("mime_type")
        private final String mimeType;

        private final String text;

        private final int weight;

        private final String uri;

        private final Map<String, String> tags;

        private final String offset;

        private final String location;

        private final String embedding;

        private final String modality;

        private final String evaluations;

        private final String scores;

        private final String chunks;

        private final String matches;
    }
}

