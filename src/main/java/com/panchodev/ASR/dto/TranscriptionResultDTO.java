package com.panchodev.ASR.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TranscriptionResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<TranscriptionTextDTO> transcripts;

    private List<TranscriptionItemDTO> items;
}
