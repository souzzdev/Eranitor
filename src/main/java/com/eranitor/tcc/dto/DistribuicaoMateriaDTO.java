package com.eranitor.tcc.dto;

public record DistribuicaoMateriaDTO (
        Long materiaId,
        String nomeMateria,
        Integer dificuldade,
        Integer horasSugeridas
) {}
