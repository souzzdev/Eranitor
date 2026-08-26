package com.eranitor.tcc.dto;

import com.eranitor.tcc.entity.Materia;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TarefaDTO(
   @NotBlank (message = "O título não pode estar vazio.")
   String titulo,
   String descricao,
   LocalDate dataVencimento,
   String cor
) {}
