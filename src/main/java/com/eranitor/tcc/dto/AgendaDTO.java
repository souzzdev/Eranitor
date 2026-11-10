package com.eranitor.tcc.dto;

import java.time.LocalDate;

public record AgendaDTO(
        Integer id,
        Long usuarioId,
        LocalDate data,
        String titulo,
        String tipo
) {}
