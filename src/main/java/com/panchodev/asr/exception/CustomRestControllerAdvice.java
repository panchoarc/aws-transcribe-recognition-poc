package com.panchodev.asr.exception;

import com.panchodev.asr.util.ApiResponse;
import com.panchodev.asr.util.ResponseBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomRestControllerAdvice {

    private static final String MESSAGE = "message";

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseBuilder.error("Validation Failed", errors);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ApiResponse<Void> handleSizeLimitException(MaxUploadSizeExceededException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, ex.getMessage());
        return ResponseBuilder.error("Exceeded File Size", errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleMethodValidationException(HandlerMethodValidationException ex) {

        Map<String, String> errors = new HashMap<>();
        errors.put(MESSAGE, "File Format Error");
        return ResponseBuilder.error("Validation Failed", errors);
    }
}
