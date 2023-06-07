package com.panchodev.ASR.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.AmazonTranscribeClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AWSConfig {

    @Value("${aws.awsAccessKey}")
    private String awsAccessKey;

    @Value("${aws.awsSecretKey}")
    private String awsSecretKey;

    @Value("${aws.awsRegion}")
    private String awsRegion;


    @Bean
    AmazonTranscribe transcribeClient() {
        log.debug("Intialize Transcribe Client");
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = getAwsStaticCredentialsProvider();
        return AmazonTranscribeClientBuilder.standard().withCredentials(awsStaticCredentialsProvider)
                .withRegion(awsRegion).build();
    }

    private AWSStaticCredentialsProvider getAwsStaticCredentialsProvider() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        return new AWSStaticCredentialsProvider(awsCreds);
    }

    @Bean
    AmazonS3 s3Client() {
        log.debug("Intialize AWS S3 Client");
        AWSStaticCredentialsProvider awsStaticCredentialsProvider = getAwsStaticCredentialsProvider();
        return AmazonS3ClientBuilder.standard().withCredentials(awsStaticCredentialsProvider).withRegion(awsRegion)
                .build();
    }
}
