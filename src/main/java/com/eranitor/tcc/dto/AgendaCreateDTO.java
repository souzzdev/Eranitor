package com.eranitor.tcc.dto;

import java.time.LocalDate;

public record AgendaCreateDTO(
        LocalDate data,
        String titulo,
        String tipo
) {}
