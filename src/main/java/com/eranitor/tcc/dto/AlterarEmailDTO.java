package com.eranitor.tcc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AlterarEmailDTO(
        @NotBlank(message = "O novo e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        String novoEmail,

        @NotBlank(message = "A senha atual é obrigatória para confirmar a alteração.")
        String senhaAtual
) {}