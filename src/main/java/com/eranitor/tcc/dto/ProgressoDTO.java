package com.eranitor.tcc.dto;

import java.util.List;

public record ProgressoDTO(
        Double progressoGeral,
        List<ProgressoMateriaDTO> materias
) {}
