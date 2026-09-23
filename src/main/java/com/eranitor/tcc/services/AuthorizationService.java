package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AuthenticationDTO;
import com.eranitor.tcc.dto.RegisterDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.enums.UsuarioRole;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.mapper.UsuarioMapper;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthorizationService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioMapper usuarioMapper;

    public String login(AuthenticationDTO data) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                data.email(),
                                data.password()
                        )
                );

        Usuario usuario =
                (Usuario) authentication.getPrincipal();

        return tokenService.generateToken(usuario);
    }

    public void register(RegisterDTO data) {

        validateEmailUnique(data.email());

        if(!data.password().equals(data.confirmPassword())) {
            throw new IllegalArgumentException("As senhas não coincidem!");
        }

        Usuario usuario = usuarioMapper.toEntity(data);

        usuario.setPassword(
                passwordEncoder.encode(data.password())
        );

        usuario.setRole(UsuarioRole.USER);
        usuario.setCriadoEm(LocalDateTime.now());

        repository.save(usuario);
    }

    public void validateToken(String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            throw new RuntimeException(
                    "Token inválido"
            );
        }
    }

    public String refreshToken(
            String authHeader) {

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            throw new RuntimeException(
                    "Token não fornecido"
            );
        }

        String token =
                authHeader.substring(7);

        String login =
                tokenService.validateTokenForRefresh(
                        token
                );

        if (login == null) {

            throw new RuntimeException(
                    "Token inválido ou expirado"
            );
        }

        Usuario usuario =
                repository.findByEmail(login)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuário não encontrado"
                                ));

        return tokenService.generateToken(usuario);
    }



    private void validateEmailUnique(String email) {
        if (repository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado!");
        }
    }
}