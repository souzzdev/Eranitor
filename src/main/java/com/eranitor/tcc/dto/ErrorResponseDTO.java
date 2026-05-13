package com.eranitor.tcc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
        int status,
        String message,
        LocalDateTime timestamp,
        List<String> errors
) {

    public ErrorResponseDTO(int status, String message) {
        this(status, message, LocalDateTime.now(), null);
    }

    public ErrorResponseDTO(int status, String message, List<String> errors) {
        this(status, message, LocalDateTime.now(), errors);
    }
}