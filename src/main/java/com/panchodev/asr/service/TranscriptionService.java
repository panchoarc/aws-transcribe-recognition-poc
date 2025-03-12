package com.panchodev.asr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panchodev.asr.dto.TranscriptionResultDTO;
import com.panchodev.asr.exception.custom.TranscriptionJobNotCompletedException;
import com.panchodev.asr.exception.custom.TranscriptionRetrievalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionService {

    private final TranscribeClient transcribeClient;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadFileToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        return "s3://" + bucketName + "/" + fileName;
    }

    public String generateJobName() {
        return "transcription-job-" + UUID.randomUUID();
    }

    public void transcribeAudio(String audioFileUri, String jobName) {
        MediaFormat mediaFormat = MediaFormat.MP3;

        String fileExtension = audioFileUri.substring(audioFileUri.lastIndexOf('.') + 1).toLowerCase();
        mediaFormat = switch (fileExtension) {
            case "wav" -> MediaFormat.WAV;
            case "ogg" -> MediaFormat.OGG;
            case "flac" -> MediaFormat.FLAC;
            default -> mediaFormat;
        };

        StartTranscriptionJobRequest transcriptionJobRequest = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .languageCode("en-US")
                .media(media -> media.mediaFileUri(audioFileUri))
                .mediaFormat(mediaFormat)
                .outputBucketName(bucketName)
                .build();

        transcribeClient.startTranscriptionJob(transcriptionJobRequest);
    }

    public TranscriptionResultDTO getTranscriptionResultDTO(String jobName) {
        try {
            GetTranscriptionJobRequest request = GetTranscriptionJobRequest.builder()
                    .transcriptionJobName(jobName)
                    .build();

            GetTranscriptionJobResponse response = transcribeClient.getTranscriptionJob(request);
            TranscriptionJob transcriptionJob = response.transcriptionJob();

            if (transcriptionJob.transcriptionJobStatus() != TranscriptionJobStatus.COMPLETED) {
                throw new TranscriptionJobNotCompletedException("El trabajo de transcripción aún no ha sido completado: " + jobName);
            }

            String transcriptFileUri = transcriptionJob.transcript().transcriptFileUri();
            URI uri = URI.create(transcriptFileUri);

            List<String> urlParts = Arrays.stream(uri.getPath().split("/"))
                    .filter(part -> !part.isEmpty())
                    .toList();

            if (urlParts.size() < 2) {
                throw new TranscriptionRetrievalException("La URL del archivo de transcripción no tiene el formato esperado: " + transcriptFileUri);
            }

            String bucket = urlParts.get(0);
            String key = urlParts.get(urlParts.size() - 1);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            return getTranscriptionResultDTO(jobName, getObjectRequest);

        } catch (SdkException e) {
            throw new TranscriptionRetrievalException(e.getMessage());
        }
    }

    private TranscriptionResultDTO getTranscriptionResultDTO(String jobName, GetObjectRequest getObjectRequest) {
        try (ResponseInputStream<GetObjectResponse> objectResponse = s3Client.getObject(getObjectRequest)) {
            String transcriptContent = new String(objectResponse.readAllBytes(), StandardCharsets.UTF_8);
            return parseTranscript(transcriptContent, jobName);
        } catch (IOException e) {
            throw new TranscriptionRetrievalException(e.getMessage());
        }
    }

    private TranscriptionResultDTO parseTranscript(String transcriptContent, String jobName) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(transcriptContent);
        String transcriptText = rootNode.path("results").path("transcripts").get(0).path("transcript").asText();

        TranscriptionResultDTO resultDTO = new TranscriptionResultDTO();
        resultDTO.setJobName(jobName);
        resultDTO.setTranscript(transcriptText);
        return resultDTO;
    }
}
