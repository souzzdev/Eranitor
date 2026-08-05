package com.eranitor.tcc.dto;

import jakarta.validation.constraints.*;

public record RegisterDTO(

        @NotBlank(message = "Email não pode estar vazio")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha não pode estar vazia")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*+=]).{8,}$",
                message = "Senha deve conter maiúsculas, minúsculas, números e caracteres especiais"
        )
        String password,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmPassword,

        @NotBlank(message = "Nome não pode estar vazio")
        @Size(min = 3, max = 100)
        String nome,

        @NotBlank(message = "Instituição não pode estar vazia")
        @Size(min = 3, max = 150)
        String instituicao,

        @NotBlank(message = "Série não pode estar vazia")
        @Size(min = 1, max = 20)
        String serie

) {
}