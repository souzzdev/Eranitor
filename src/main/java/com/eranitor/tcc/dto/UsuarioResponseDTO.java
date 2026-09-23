package com.eranitor.tcc.dto;

import com.eranitor.tcc.enums.UsuarioRole;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long idUsuario,
        String nome,
        String email,
        String serie,
        String instituicao,
        LocalDateTime criadoEm,
        UsuarioRole role
) {
}
