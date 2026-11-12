package com.eranitor.tcc.dto;

import com.eranitor.tcc.entity.Topico;

public record TopicoResponseDTO(
        Long idTopico,
        String nome,
        Boolean concluido
) {

    public static TopicoResponseDTO from(Topico topico) {
        return new TopicoResponseDTO(
                topico.getIdTopico(),
                topico.getNome(),
                topico.getConcluido()
        );
    }
}
