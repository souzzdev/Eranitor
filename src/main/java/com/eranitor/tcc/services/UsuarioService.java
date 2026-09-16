package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AlterarEmailDTO;
import com.eranitor.tcc.dto.AlterarSenhaDTO;
import com.eranitor.tcc.dto.AtualizarPerfilDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    UsuarioRepository repository;

    @Autowired
    private PasswordEncoder  passwordEncoder;


    public ResponseEntity<?> findByLogin(String login) {
        Optional<Usuario> usuario = repository.findByEmail(login);

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

    public Usuario getPerfil (String email) {
        return repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));
    }

    public Usuario updatePerfil (String email, AtualizarPerfilDTO dto) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado"));


        usuario.setNome(dto.nome());
        usuario.setInstituicao(dto.instituicao());
        usuario.setSerie(dto.serie());

        return repository.save(usuario);
    }

    public void alterarEmail(String emailAtual, AlterarEmailDTO dto) {
        Usuario usuario = repository.findByEmail(emailAtual)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getPassword())) {
            throw new IllegalArgumentException("Senha incorreta.");
        }

        if (repository.existsByEmail(dto.novoEmail())) {
            throw new IllegalArgumentException("O novo e-mail informado já está em uso.");
        }

        usuario.setEmail(dto.novoEmail());
        repository.save(usuario);
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
