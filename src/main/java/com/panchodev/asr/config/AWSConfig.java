package com.panchodev.asr.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.TranscribeClientBuilder;

import java.net.URI;

@Configuration
@Slf4j
public class AWSConfig {

    @Value("${aws.s3.localstackEndpoint:}")
    private String s3LocalStackEndpoint;

    @Value("${aws.s3.region}")
    private String s3Region;

    @Value("${aws.s3.useLocalStack}")
    private boolean useLocalStack;


    @Value("${aws.transcribe.region}")
    private String transcribeRegion;

    @Value("${aws.transcribe.localstackEndpoint:}")
    private String transcribeLocalStackEndpoint;


    @Bean
    public AwsCredentialsProvider credentialsProvider() {
        return ProfileCredentialsProvider.create();
    }

    @Bean
    public TranscribeClient transcribeClient() {
        TranscribeClientBuilder transcribeClientBuilder = TranscribeClient.builder()
                .region(Region.of(transcribeRegion))
                .credentialsProvider(credentialsProvider());
        if (useLocalStack && !s3LocalStackEndpoint.isEmpty()) {
            transcribeClientBuilder.endpointOverride(URI.create(transcribeLocalStackEndpoint));
        }

        return transcribeClientBuilder.build();
    }

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder s3ClientBuilder = S3Client.builder()
                .region(Region.of(s3Region))
                .credentialsProvider(credentialsProvider());

        if (useLocalStack && !s3LocalStackEndpoint.isEmpty()) {
            s3ClientBuilder.endpointOverride(URI.create(s3LocalStackEndpoint));
        }

        return s3ClientBuilder.build();
    }

}
