package com.eranitor.tcc.dto;

public record TempoEstudoDTO(
        Integer id,
        Long usuarioId,
        Long materiaId,
        Integer mes,
        Integer ano,
        Integer minutosAtividade,
        Integer minutosVideo,
        Integer minutosApostila
) {}