package com.panchodev.ASR.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panchodev.ASR.dto.TranscriptionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.*;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptionService {

    private final TranscribeClient transcribeClient;

    @Value("${aws.bucketName}")
    private String bucketName;

    // -----------------------------------------
    // START JOB
    // -----------------------------------------
    public String startTranscriptionJob(String key) {

        String jobName = UUID.randomUUID() + "-" +
                key.replaceAll("[^a-zA-Z0-9._-]", "_");

        String mediaUri = "s3://" + bucketName + "/" + key;

        String ext = key.substring(key.lastIndexOf('.') + 1).toLowerCase();

        MediaFormat format = switch (ext) {
            case "mp3" -> MediaFormat.MP3;
            case "wav" -> MediaFormat.WAV;
            case "flac" -> MediaFormat.FLAC;
            case "ogg" -> MediaFormat.OGG;
            default -> throw new IllegalArgumentException("Unsupported format");
        };

        StartTranscriptionJobRequest request = StartTranscriptionJobRequest.builder()
                .transcriptionJobName(jobName)
                .media(Media.builder().mediaFileUri(mediaUri).build())
                .mediaFormat(format)
                .languageCode(LanguageCode.ES_ES)
                .build();

        transcribeClient.startTranscriptionJob(request);

        log.info("Started job: {}", jobName);

        return jobName;
    }

    // -----------------------------------------
    // GET JOB (RAW)
    // -----------------------------------------
    public TranscriptionJob getJob(String jobName) {

        return transcribeClient.getTranscriptionJob(
                r -> r.transcriptionJobName(jobName)
        ).transcriptionJob();
    }

    // -----------------------------------------
    // GET STATUS ONLY
    // -----------------------------------------
    public TranscriptionJobStatus getJobStatus(String jobName) {
        return getJob(jobName).transcriptionJobStatus();
    }

    // -----------------------------------------
    // DOWNLOAD RESULT JSON
    // -----------------------------------------
    public TranscriptionResponseDTO downloadTranscriptionResponse(String uri) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. Obtener respuesta como String (RAW)
            String json = restTemplate.getForObject(uri, String.class);

            // 2. Parsear manualmente con Jackson
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(json, TranscriptionResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse transcription response", e);
        }
    }
}