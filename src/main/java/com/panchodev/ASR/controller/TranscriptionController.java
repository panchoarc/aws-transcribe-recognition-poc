package com.panchodev.ASR.controller;

import com.amazonaws.services.transcribe.model.GetTranscriptionJobResult;
import com.amazonaws.services.transcribe.model.StartTranscriptionJobResult;
import com.panchodev.ASR.dto.TranscriptionResponseDTO;
import com.panchodev.ASR.helpers.AwsBucket;
import com.panchodev.ASR.service.TranscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/extract")
@RequiredArgsConstructor
public class TranscriptionController {


    private final AwsBucket awsBucketHelper;

    private final TranscriptionService transcriptionService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;


    @Tag(name = "Transcription", description = "Transcription APIs")
    @Operation(
            summary = "Retrieve the content of an audio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transcription response", content = {@Content(schema = @Schema(implementation = TranscriptionResponseDTO.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "413", description = "File size exceeded", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping(value = "/extractFromFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> extractSpeechTextFromVideo(@Parameter(description = "Audio file to analyze") @RequestPart("file") MultipartFile file) {

        log.info("Request to extract Speech Text from Video : {}", file.getContentType());

        if (file.isEmpty()) {
            // Handle the case when no file is sent
            // Return an error response or throw an exception
            return new ResponseEntity<>("You must select a file!", HttpStatus.BAD_REQUEST);
        }

        // Check file size
        long fileSizeInBytes = file.getSize();
        long maxFileSizeInBytes = Long.parseLong(maxFileSize); // 50 MB
        if (fileSizeInBytes > maxFileSizeInBytes) {
            return new ResponseEntity<>("File size exceeds the maximum allowed limit of " + maxFileSize + " MB.", HttpStatus.PAYLOAD_TOO_LARGE);
        }

        String contentType = file.getContentType();
        if (!isSupportedContentType(contentType)) {
            // Handle the case when the file is not an audio file
            // Return an error response or throw an exception
            return new ResponseEntity<>("Invalid file type. Only audio files are allowed.", HttpStatus.BAD_REQUEST);
        }

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

        return ResponseEntity.ok(transcriptionResponseDTO);
    }


    private boolean isSupportedContentType(String contentType) {
        String audioMimeTypePattern = "^audio/.*$";
        Pattern pattern = Pattern.compile(audioMimeTypePattern);
        Matcher matcher = pattern.matcher(contentType);
        return matcher.find();
    }
}
