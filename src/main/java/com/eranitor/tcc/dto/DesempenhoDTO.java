package com.eranitor.tcc.dto;

public record DesempenhoDTO(
        Long id,
        Long usuarioId,
        Long materiaId,
        Integer mes,
        Integer ano,
        Integer horasConcluidas,
        Integer horasMeta
) {}