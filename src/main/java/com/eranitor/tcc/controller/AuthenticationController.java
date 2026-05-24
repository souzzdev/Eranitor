package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.AuthenticationDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.LoginResponseDTO;
import com.eranitor.tcc.dto.RegisterDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.enums.UsuarioRole;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepositoy;
import io.jsonwebtoken.security.Password;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepositoy repositoy;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data) {
        try {

            Optional<Usuario> usuarioOpt = repositoy.findByLogin(data.email());
            System.out.println("Usuário encontrado: " + usuarioOpt.isPresent());

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponseDTO(401, "Email ou senha inválidos"));
            }

            Usuario usuario = usuarioOpt.get();
            System.out.println("Senha no DB: " + usuario.getPassword());
            System.out.println("Match: " + passwordEncoder.matches(data.password(), usuario.getPassword()));

            if (!passwordEncoder.matches(data.password(), usuario.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponseDTO(401, "Email ou senha inválidos"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.email(), data.password())
            );

            Usuario usuarioAutenticado = (Usuario) authentication.getPrincipal();
            String token = (usuarioAutenticado != null) ? tokenService.generateToken(usuarioAutenticado) : null;

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO(401, "Email ou senha inválidos"));
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(500, "Erro ao fazer login: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity register (@RequestBody @Valid RegisterDTO data) {
        try {
            if(this.repositoy.findByLogin(data.email()).isPresent()) return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(
                    409,
                    "Email já cadastrado no sistema"
            ));
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encryptedPassword = passwordEncoder.encode(data.password());

            Usuario newUsuario = new Usuario();

            newUsuario.setLogin(data.email());
            newUsuario.setPassword(encryptedPassword);
            newUsuario.setNome(data.nome());
            newUsuario.setInstituicao(data.instituicao());
            newUsuario.setSerie(data.serie());
            newUsuario.setRole(UsuarioRole.USER);
            newUsuario.setCriadoEm(LocalDateTime.now());

            this.repositoy.save(newUsuario);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ErrorResponseDTO(
                            201,
                            "Usuario registrado comn sucesso"
                    ));
        } catch (Exception exception) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(
                            500,
                            "Erro ao registrar usuario: " + exception.getMessage()
                    ));
        }


    }


    @GetMapping("/validate")
    public ResponseEntity<?> validateToken (@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponseDTO(
                                401,
                                "Token inválido ou expirado"
                        ));
            }

            return ResponseEntity.ok("Token válido");
        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(
                            500,
                            "Erro ao validar token"
                    ));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken (@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponseDTO(
                                401,
                                "Token não fornecido"
                        ));
            }

            String token = authHeader.substring(7);

            String login = tokenService.validateTokenForRefresh(token);

            if (login == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponseDTO(
                                401,
                                "Token inválido ou expirado"
                        ));
            }

            var usuarioOpt = repositoy.findByLogin(login);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponseDTO(
                                404,
                                "Usuário não encontrado"
                        ));
            }

            Usuario usuario = usuarioOpt.get();

            String newToken = tokenService.generateToken(usuario);

            return ResponseEntity.ok(new LoginResponseDTO(newToken));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO(
                            500,
                            "Erro ao renovar token."
                    ));
        }
    }
}
