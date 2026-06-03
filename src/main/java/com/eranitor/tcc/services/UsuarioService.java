package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.UsuarioRepositoy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepositoy repository;

    public ResponseEntity<?> cadastrarUsuario (Usuario usuario) {
        if (repository.existsById(usuario.getIdUsuario())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(
                            409,
                            "O usuário já existe!"
                    ));
        }

        Usuario usuarioSalvo = repository.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioSalvo);
    }
}
