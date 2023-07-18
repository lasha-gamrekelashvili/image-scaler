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

    @JsonProperty("header")
    private Header header;

    @JsonProperty("parameters")
    private Map<String, String> parameters;

    @JsonProperty("routes")
    private List<Route> routes;

    @JsonProperty("data")
    private List<DataItem> data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        @JsonProperty("requestId")
        private String requestId;

        @JsonProperty("status")
        private String status;

        @JsonProperty("execEndpoint")
        private String execEndpoint;

        @JsonProperty("targetExecutor")
        private String targetExecutor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Route {
        @JsonProperty("executor")
        private String executor;

        @JsonProperty("startTime")
        private String startTime;

        @JsonProperty("endTime")
        private String endTime;

        @JsonProperty("status")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataItem {
        @JsonProperty("id")
        private String id;

        @JsonProperty("parent_id")
        private String parentId;

        @JsonProperty("granularity")
        private String granularity;

        @JsonProperty("adjacency")
        private String adjacency;

        @JsonProperty("blob")
        private String blob;

        @JsonProperty("tensor")
        private String tensor;

        @JsonProperty("mime_type")
        private String mimeType;

        @JsonProperty("text")
        private String text;

        @JsonProperty("weight")
        private int weight;

        @JsonProperty("uri")
        private String uri;

        @JsonProperty("tags")
        private Map<String, String> tags;

        @JsonProperty("offset")
        private String offset;

        @JsonProperty("location")
        private String location;

        @JsonProperty("embedding")
        private String embedding;

        @JsonProperty("modality")
        private String modality;

        @JsonProperty("evaluations")
        private String evaluations;

        @JsonProperty("scores")
        private String scores;

        @JsonProperty("chunks")
        private String chunks;

        @JsonProperty("matches")
        private String matches;
    }
}

