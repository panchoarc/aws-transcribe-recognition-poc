package com.panchodev.asr.exception.custom;

public class TranscriptionJobNotCompletedException extends RuntimeException {
    public TranscriptionJobNotCompletedException(String message) {
        super(message);
    }
}
