package com.eranitor.tcc.dto;

import java.util.List;

public record RelatorioDTO(
        String periodo,
        List<DesempenhoDTO> desempenho,
        List<TempoEstudoDTO> tempoEstudo,
        List<AgendaDTO> eventos
) {}
