package com.panchodev.asr.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.transcribe.TranscribeClient;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AWSConfig {

    private final AwsProperties props;

    @Bean
    public S3Client s3Client() {

        var builder = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentials());

        if (props.isUseLocalStack()) {
            log.info("Using LocalStack S3");

            builder.endpointOverride(URI.create(props.getS3().getEndpoint()))
                    .serviceConfiguration(
                            S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build()
                    );
        }

        return builder.build();
    }

    @Bean
    public TranscribeClient transcribeClient() {

        var builder = TranscribeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentials());

        if (props.isUseLocalStack()) {
            log.info("Using LocalStack Transcribe");
            builder.endpointOverride(URI.create(props.getTranscribe().getEndpoint()));
        }

        return builder.build();
    }

    private AwsCredentialsProvider credentials() {

        if (props.isUseLocalStack()) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create("test", "test")
            );
        }

        return DefaultCredentialsProvider.create();
    }
}