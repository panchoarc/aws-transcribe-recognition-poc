package com.panchodev.ASR.controller;

import com.panchodev.ASR.dto.TranscriptionResponseDTO;
import com.panchodev.ASR.helpers.AwsBucket;
import com.panchodev.ASR.service.TranscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.transcribe.model.TranscriptionJobStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/extract")
@RequiredArgsConstructor
public class TranscriptionController {

    private final AwsBucket awsBucketHelper;

    private final TranscriptionService transcriptionService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;

    private final List<String> supportedMimeTypes = Arrays.asList(
            "audio/mpeg",
            "audio/wav",
            "audio/x-wav",
            "audio/ogg",
            "audio/flac"
    );

    @Tag(name = "Transcription", description = "Transcription APIs")
    @Operation(summary = "Retrieve the content of an audio")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Transcription response",
                    content = {
                            @Content(
                                    schema = @Schema(
                                            implementation = TranscriptionResponseDTO.class
                                    ),
                                    mediaType = "application/json"
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart("file") MultipartFile file) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (file.getSize() > maxFileSize.toBytes()) {
                return ResponseEntity
                        .status(HttpStatusCode.valueOf(413))
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Payload too large");
            }

            if (!supportedMimeTypes.contains(file.getContentType())) {
                return ResponseEntity.badRequest().body("Unsupported format");
            }

            String key = file.getOriginalFilename()
                    .replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9._-]", "_")
                    .toLowerCase();

            awsBucketHelper.uploadFileToAwsBucket(file, key);

            String jobName = transcriptionService.startTranscriptionJob(key);

            return ResponseEntity.accepted().body(
                    Map.of(
                            "jobName", jobName,
                            "status", "PROCESSING"
                    )
            );

        } catch (Exception e) {
            log.error("Upload error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // -----------------------------------------
    // 2. CHECK STATUS
    // -----------------------------------------
    @GetMapping("/job/{jobName}")
    public ResponseEntity<?> getStatus(@PathVariable String jobName) {

        TranscriptionJobStatus status =
                transcriptionService.getJobStatus(jobName);

        return ResponseEntity.ok(Map.of(
                "jobName", jobName,
                "status", status.toString()
        ));
    }

    // -----------------------------------------
    // 3. GET RESULT (IF READY)
    // -----------------------------------------
    @GetMapping("/job/{jobName}/result")
    public ResponseEntity<?> getResult(@PathVariable String jobName) {

        var job = transcriptionService.getJob(jobName);

        if (job.transcriptionJobStatus() != TranscriptionJobStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "status", job.transcriptionJobStatus().toString(),
                            "message", "Transcription not ready yet"
                    ));
        }

        String uri = job.transcript().transcriptFileUri();

        TranscriptionResponseDTO dto =
                transcriptionService.downloadTranscriptionResponse(uri);

        return ResponseEntity.ok(dto);
    }
}