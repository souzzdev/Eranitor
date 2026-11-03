package com.eranitor.tcc.dto;

import java.time.LocalDate;

public record TarefaCreateDTO (
        String titulo,
        String descricao,
        LocalDate dataVencimento,
        Integer dificuldade,
        Integer materiaId,
        Integer topicoId
){
}
