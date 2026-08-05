package com.eranitor.tcc.dto;

import jakarta.validation.constraints.NotBlank;

public record MateriaDTO(
        @NotBlank(message = "O nome da matéria é obrigatório.")
        String nome,
        Boolean ativa
) {
}
