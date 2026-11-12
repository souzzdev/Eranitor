package com.eranitor.tcc.dto;

public record LoginResponseDTO(
        String token,
        String refreshToken,
        UsuarioResponseDTO usuario
) {

    public LoginResponseDTO(String token) {
        this(token, null, null);
    }

    public LoginResponseDTO(String token, UsuarioResponseDTO usuario) {
        this(token, null, usuario);
    }
}
