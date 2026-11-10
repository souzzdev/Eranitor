package com.eranitor.tcc.dto;

import java.time.LocalDate;

public record TarefaPriorizadaDTO(
        Long idTarefa,
        String titulo,
        String descricao,
        LocalDate dataVencimento,
        String cor,
        Boolean concluida,
        Double prioridadeCalculada,
        Long materiaId,
        String nomeMateria,
        Long usuarioId
) {}
