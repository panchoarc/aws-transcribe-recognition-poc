package com.panchodev.ASR.helpers;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsBucket {

    private final AmazonS3 s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    public void uploadFileToAwsBucket(MultipartFile file) {
        log.debug("Upload file to AWS Bucket {}", file);
        String key = Objects.requireNonNull(file.getOriginalFilename()).replaceAll(" ", "_").toLowerCase();
        try {
            s3Client.putObject(bucketName, key, file.getInputStream(), null);
        } catch (SdkClientException | IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFileFromAwsBucket(String fileName) {
        log.debug("Delete File from AWS Bucket {}", fileName);
        String key = fileName.replaceAll(" ", "_").toLowerCase();
        s3Client.deleteObject(bucketName, key);
    }
}
