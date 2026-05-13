package com.eranitor.tcc.dto;

import jakarta.validation.constraints.*;

public record RegisterDTO(
        @NotBlank(message = "Email não pode estar vazio")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha não pode estar vazia")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])",
                message = "Senha deve conter maiúsculas, minúsculas, números e caracteres especiais"
        )
        String password,

        @NotBlank(message = "Nome não pode estar vazio")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String nome,

        @NotBlank(message = "Instituição não pode estar vazia")
        @Size(min = 3, max = 150, message = "Instituição deve ter entre 3 e 150 caracteres")
        String instituicao,

        @NotBlank(message = "Série/Nível não pode estar vazio")
        @Size(min = 1, max = 20, message = "Série deve ter entre 1 e 20 caracteres")
        String serie
) {
}