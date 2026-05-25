package com.panchodev.ASR.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {

    private boolean useLocalStack;
    private String region;
    private String accessKey;
    private String secretKey;

    private Service s3;
    private Service transcribe;

    @Data
    public static class Service {
        private String endpoint;
    }

    private String bucketName;

}