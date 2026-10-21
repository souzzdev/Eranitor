package com.eranitor.tcc.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MateriaDTO(
        @NotBlank(message = "O nome da matéria é obrigatório.")
        String nome,
        Boolean ativa,
        @NotNull(message = "A dificuldade da matéria é obrigatória.")
        @Min(value = 1, message = "A dificuldade mínima é 1.")
        @Max(value = 5, message = "A dificuldade máxima é 5.")
        Integer dificuldade
) {
}
