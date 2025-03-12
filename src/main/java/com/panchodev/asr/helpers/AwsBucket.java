package com.panchodev.asr.helpers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsBucket {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public void uploadFileToAwsBucket(MultipartFile file) {

        String imageName = Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_").toLowerCase();

        try {

            ensureBucketExists();
            byte[] fileBytes = file.getBytes(); // Convertir a bytes correctamente
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageName)
                    .contentType(file.getContentType())
                    .contentLength((long) fileBytes.length) // Asegurar que el tamaño sea correcto
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(fileBytes));
        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getLocalizedMessage());
        }
    }

    public void deleteFileFromAwsBucket(String fileName) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private void ensureBucketExists() {
        try {
            ListBucketsResponse response = s3Client.listBuckets();

            boolean bucketExists = response.buckets().stream()
                    .anyMatch(bucket -> bucket.name().equals(bucketName));

            if (bucketExists) {
                log.info("Bucket already exists: {}", bucketName);
            } else {
                log.info("Bucket does not exist, creating: {}", bucketName);
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Error while checking or creating bucket: {}", e.getMessage(), e);
        }
    }


}