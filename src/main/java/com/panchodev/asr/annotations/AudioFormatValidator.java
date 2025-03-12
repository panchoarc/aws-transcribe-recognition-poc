package com.panchodev.asr.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class AudioFormatValidator implements ConstraintValidator<ValidAudioFormat, MultipartFile> {

    @Value("${spring.servlet.multipart.max-file-size}")
    private DataSize maxFileSize;


    private final List<String> supportedExtensions = Arrays.asList("mp3", "wav", "ogg", "flac", "mpeg");
    private final List<String> supportedMimeTypes = Arrays.asList("audio/mpeg", "audio/wav", "audio/ogg", "audio/x-flac");

    @Override
    public void initialize(ValidAudioFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            return false;
        }

        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        String contentType = file.getContentType();

        if (!supportedExtensions.contains(fileExtension) || (contentType != null && !supportedMimeTypes.contains(contentType))) {
            return false;
        }

        return file.getSize() <= maxFileSize.toBytes();
    }
}
