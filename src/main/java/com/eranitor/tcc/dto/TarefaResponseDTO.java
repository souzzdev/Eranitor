package com.eranitor.tcc.dto;

import com.eranitor.tcc.entity.Tarefa;

import java.time.LocalDate;

public record TarefaResponseDTO(
        Long idTarefa,
        String titulo,
        String descricao,
        LocalDate dataVencimento,
        String cor,
        Boolean concluida,
        Long materiaId
) {

    public static TarefaResponseDTO from(Tarefa tarefa) {
        return new TarefaResponseDTO(
                tarefa.getIdTarefa(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getDataVencimento(),
                tarefa.getCor(),
                tarefa.getConcluida(),
                tarefa.getMateria() != null ? tarefa.getMateria().getIdMateria() : null
        );
    }
}
