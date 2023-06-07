package com.panchodev.ASR.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.transcribe.AmazonTranscribe;
import com.amazonaws.services.transcribe.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panchodev.ASR.dto.TranscriptionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TranscriptionService {


    private final AmazonS3 s3Client;

    private final AmazonTranscribe transcribeClient;

    @Value("${aws.bucketName}")
    private String bucketName;

    public StartTranscriptionJobResult startTranscriptionJob(String key) {
        log.debug("Start Transcription Job By Key {}", key);
        String uuid = UUID.randomUUID().toString();


        Media media = new Media().withMediaFileUri(s3Client.getUrl(bucketName, key).toExternalForm());
        String jobName = uuid.concat(key);
        StartTranscriptionJobRequest startTranscriptionJobRequest = new StartTranscriptionJobRequest()
                .withLanguageCode(LanguageCode.EnUS).withTranscriptionJobName(jobName).withMedia(media);
        return transcribeClient
                .startTranscriptionJob(startTranscriptionJobRequest);
    }


    public GetTranscriptionJobResult getTranscriptionJobResult(String jobName) {
        log.debug("Get Transcription Job Result By Job Name : {}", jobName);
        GetTranscriptionJobRequest getTranscriptionJobRequest = new GetTranscriptionJobRequest()
                .withTranscriptionJobName(jobName);
        boolean resultFound = false;
        TranscriptionJob transcriptionJob;
        GetTranscriptionJobResult getTranscriptionJobResult = new GetTranscriptionJobResult();
        while (!resultFound) {
            getTranscriptionJobResult = transcribeClient.getTranscriptionJob(getTranscriptionJobRequest);
            transcriptionJob = getTranscriptionJobResult.getTranscriptionJob();
            if (transcriptionJob.getTranscriptionJobStatus()
                    .equalsIgnoreCase(TranscriptionJobStatus.COMPLETED.name())) {
                return getTranscriptionJobResult;
            } else if (transcriptionJob.getTranscriptionJobStatus()
                    .equalsIgnoreCase(TranscriptionJobStatus.FAILED.name())) {
                return null;
            } else if (transcriptionJob.getTranscriptionJobStatus()
                    .equalsIgnoreCase(TranscriptionJobStatus.IN_PROGRESS.name())) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.debug("Interrupted Exception {}", e.getMessage());
                }
            }
        }
        return getTranscriptionJobResult;
    }

    /******************Step 6 **************************
     *  Download Transcription Result from URI Method *********/

    public TranscriptionResponseDTO downloadTranscriptionResponse(String uri) {
        log.debug("Download Transcription Result from Transcribe URi {}", uri);
        OkHttpClient okHttpClient = new OkHttpClient()
                .newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder().url(uri).build();
        Response response;
        try {
            response = okHttpClient.newCall(request).execute();
            String body = Objects.requireNonNull(response.body()).string();
            ObjectMapper objectMapper = new ObjectMapper();
            response.close();
            return objectMapper.readValue(body, TranscriptionResponseDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***************************** Step 7 *****************************
     * **** Delete Transcription Job Method ************
     * TO delete transcription job after getting result****/

    public void deleteTranscriptionJob(String jobName) {
        log.debug("Delete Transcription Job from amazon Transcribe {}", jobName);
        DeleteTranscriptionJobRequest deleteTranscriptionJobRequest = new DeleteTranscriptionJobRequest()
                .withTranscriptionJobName(jobName);
        transcribeClient.deleteTranscriptionJob(deleteTranscriptionJobRequest);
    }
}
