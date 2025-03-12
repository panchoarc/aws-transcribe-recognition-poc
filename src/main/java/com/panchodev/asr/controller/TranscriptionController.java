package com.panchodev.asr.controller;

import com.panchodev.asr.annotations.ValidAudioFormat;
import com.panchodev.asr.dto.TranscriptionResultDTO;
import com.panchodev.asr.service.TranscriptionService;
import com.panchodev.asr.util.ApiResponse;
import com.panchodev.asr.util.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/transcribe")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> startTranscription(@RequestPart @ValidAudioFormat MultipartFile file) throws IOException {

        String audioFileUri = transcriptionService.uploadFileToS3(file);
        String jobName = transcriptionService.generateJobName();

        transcriptionService.transcribeAudio(audioFileUri, jobName);

        return ResponseBuilder.success("Transcripción en curso", jobName);
    }

    @GetMapping("/result/{jobName}")
    public ApiResponse<TranscriptionResultDTO> getTranscriptionResult(@PathVariable String jobName) {

        TranscriptionResultDTO transcriptionResult = transcriptionService.getTranscriptionResultDTO(jobName);

        return ResponseBuilder.success("", transcriptionResult);
    }
}
