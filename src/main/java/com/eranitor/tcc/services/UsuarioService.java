package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AlterarSenhaDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.UsuarioRepositoy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepositoy repository;

    @Autowired
    private PasswordEncoder  passwordEncoder;


    public ResponseEntity<?> findByLogin(String login) {
        Optional<Usuario> usuario = repository.findByLogin(login);

        if (usuario.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO(
                            404,
                            "Usuário não encontrado!"
                    ));
        }

        return ResponseEntity.ok(usuario.get());
    }

    public Usuario getPerfil (String login) {
        return repository.findByLogin(login)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));
    }

    public Usuario updatePerfil (String login, Usuario novosDados) {
        Usuario usuario = repository.findByLogin(login)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));

        usuario.setLogin(novosDados.getLogin());
        usuario.setNome(novosDados.getNome());
        usuario.setInstituicao(novosDados.getInstituicao());
        usuario.setSerie(novosDados.getSerie());

        return repository.save(usuario);
    }

    public void alterarSenha (Long id, AlterarSenhaDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        boolean senhaOk = passwordEncoder.matches(dto.senhaAtual(), usuario.getPassword());
        if (!senhaOk) {
            throw new RuntimeException("Senha atual incorreta!");
        }

        if (passwordEncoder.matches(dto.novaSenha(), usuario.getPassword())) {
            throw new IllegalArgumentException("A nova senha deve ser difernte da senha atual!");
        }

        if(!dto.novaSenha().equals(dto.confirmarSenha())) {
            throw new IllegalArgumentException("As senhas não coincidem!");
        }

        usuario.setPassword(passwordEncoder.encode(dto.novaSenha()));

        repository.save(usuario);
    }


}
