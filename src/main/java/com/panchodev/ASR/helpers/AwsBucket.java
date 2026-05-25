package com.panchodev.ASR.helpers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AwsBucket {

    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    public void uploadFileToAwsBucket(
            MultipartFile file,
            String key
    ) {

        try {

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            log.info("File uploaded to S3: {}", key);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error uploading file to S3",
                    e
            );
        }
    }

    public void deleteFileFromAwsBucket(String key) {

        DeleteObjectRequest deleteObjectRequest =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

        s3Client.deleteObject(deleteObjectRequest);

        log.info("File deleted from S3: {}", key);
    }
}