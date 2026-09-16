package com.eranitor.tcc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TarefaDTO(
   @NotBlank (message = "O título não pode estar vazio.")
   String titulo,

   @NotBlank(message = "A descrição não pode ser nula ou em branco.")
   String descricao,

   @NotNull(message = "A data de vencimento é obrigatória.")
   LocalDate dataVencimento,

   @NotBlank(message = "A cor deve ser definida.")
   String cor

) {}
