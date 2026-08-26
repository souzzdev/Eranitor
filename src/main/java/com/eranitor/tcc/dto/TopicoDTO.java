package com.eranitor.tcc.dto;

import jakarta.validation.constraints.NotBlank;

public record TopicoDTO(
        @NotBlank (message = "O nome não pode estar vazio.")
        String nome,
        Boolean concluido
) {}
