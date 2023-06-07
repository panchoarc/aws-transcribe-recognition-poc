package com.panchodev.ASR.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionResponseDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    private String jobName;

    private String accountId;

    private TranscriptionResultDTO results;

    private String status;
}
