package com.panchodev.ASR.controller;

import com.amazonaws.services.transcribe.model.GetTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobResult;
import com.panchodev.ASR.dto.TranscriptionResponseDTO;
import com.panchodev.ASR.helpers.AwsBucket;
import com.panchodev.ASR.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/extract")
@RequiredArgsConstructor
public class TranscriptionController {


    private final AwsBucket awsBucketHelper;

    private final TranscriptionService transcriptionService;


    @PostMapping("/extractFromFile")
    public TranscriptionResponseDTO extractSpeechTextFromVideo(MultipartFile file) {
        log.debug("Request to extract Speech Text from Video : {}", file);

        // Upload file to Aws
        awsBucketHelper.uploadFileToAwsBucket(file);

        // Create a key that is like name for file and will be used for creating unique name based id for transcription job
        String key = Objects.requireNonNull(file.getOriginalFilename()).replaceAll(" ", "_").toLowerCase();

        // Start Transcription Job and get result
        StartTranscriptionJobResult startTranscriptionJobResult = transcriptionService.startTranscriptionJob(key);


        // Get name of job started for the file
        String transcriptionJobName = startTranscriptionJobResult.getTranscriptionJob().getTranscriptionJobName();

        // Get result after the processing is complete
        GetTranscriptionJobResult getTranscriptionJobResult = transcriptionService.getTranscriptionJobResult(transcriptionJobName);

        //delete file as processing is done
        awsBucketHelper.deleteFileFromAwsBucket(key);

        // Url of result file for transcription
        String transcriptFileUriString = getTranscriptionJobResult.getTranscriptionJob().getTranscript().getTranscriptFileUri();

        // Get the transcription response by downloading the file
        TranscriptionResponseDTO transcriptionResponseDTO = transcriptionService.downloadTranscriptionResponse(transcriptFileUriString);

        //Delete the transcription job after finishing, or it will get deleted after 90 days automatically if you do not call
        transcriptionService.deleteTranscriptionJob(transcriptionJobName);

        return transcriptionResponseDTO;
    }
}
