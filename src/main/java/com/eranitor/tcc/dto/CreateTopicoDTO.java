package com.eranitor.tcc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTopicoDTO(
        @NotBlank(message = "O nome do tópico é obrigatório.")
        @Size(max = 150, message = "O nome do tópico deve ter no máximo 150 caracteres.")
        String nome
) {
}
