package com.panchodev.asr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptionResponseDTO {

    @JsonProperty("jobName")
    private String jobName;

    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("results")
    private Results results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Results {

        @JsonProperty("transcripts")
        private List<Transcript> transcripts;

        @JsonProperty("items")
        private List<Item> items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Transcript {

        @JsonProperty("transcript")
        private String transcript;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {

        @JsonProperty("start_time")
        private String startTime;

        @JsonProperty("end_time")
        private String endTime;

        @JsonProperty("alternatives")
        private List<Alternative> alternatives;

        @JsonProperty("type")
        private String type;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Alternative {

        @JsonProperty("confidence")
        private String confidence;

        @JsonProperty("content")
        private String content;
    }
}