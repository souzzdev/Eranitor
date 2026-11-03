package com.eranitor.tcc.dto;

public record TempoEstudoRegistroDTO(
        String tipo,
        Integer minutos,
        Long materiaId
) {}