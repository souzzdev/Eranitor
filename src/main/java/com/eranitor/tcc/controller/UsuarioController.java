package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.AlterarEmailDTO;
import com.eranitor.tcc.dto.AlterarSenhaDTO;
import com.eranitor.tcc.dto.AtualizarPerfilDTO;
import com.eranitor.tcc.dto.UsuarioResponseDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login/{login}")
    public ResponseEntity<?> findByLogin(
            @PathVariable String login) {

        return usuarioService.findByLogin(login);
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioResponseDTO> getPerfil(
            @RequestParam String email) {

        return ResponseEntity.ok(
                usuarioService.getPerfil(email)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getMe(
            Authentication authentication) {

        Usuario usuario = (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(
                usuarioService.getPerfil(usuario.getEmail())
        );
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioResponseDTO> updatePerfil(
            @RequestBody AtualizarPerfilDTO dto,
            @RequestParam String email) {

        return ResponseEntity.ok(
                usuarioService.updatePerfil(email, dto)
        );
    }

    @PostMapping("/email")
    public ResponseEntity<?> alterarEmail(
            @RequestBody AlterarEmailDTO dto,
            @RequestParam String emailAtual) {

        try {
            usuarioService.alterarEmail(emailAtual, dto);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/{id}/senha")
    public ResponseEntity<?> alterarSenha(
            @PathVariable Long id,
            @RequestBody AlterarSenhaDTO dto) {

        try {
            usuarioService.alterarSenha(id, dto);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage()
                    ));
        }
    }
}
