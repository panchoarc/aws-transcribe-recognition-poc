package com.panchodev.ASR.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscriptionItemDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String start_time;

    private String end_time;

    private List<TranscriptionItemAlternativesDTO> alternatives;

    private String type;
}
