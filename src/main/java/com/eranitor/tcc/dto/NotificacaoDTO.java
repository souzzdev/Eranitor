package com.eranitor.tcc.dto;

import java.util.List;

public record NotificacaoDTO(
        String mensagem,
        Double desempenho,
        List<String> tarefasProximas
) {}
