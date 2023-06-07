package com.panchodev.ASR.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionItemAlternativesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String confidence;

    private String content;
}
