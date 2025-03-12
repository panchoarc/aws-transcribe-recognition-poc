package com.panchodev.asr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String jobName;
    private String transcript;
}
