package com.eranitor.tcc.dto;

import jakarta.validation.constraints.NotBlank;

public record AtualizarPerfilDTO(
        @NotBlank(message = "O nome não pode estar em branco.")
        String nome,

        @NotBlank(message = "A instituição não pode estar em branco.")
        String instituicao,

        @NotBlank(message = "A série não pode estar em branco.")
        String serie
) {}