package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.AuthenticationDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.LoginResponseDTO;
import com.eranitor.tcc.dto.RegisterDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.enums.UsuarioRole;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepositoy;
import com.eranitor.tcc.services.AuthorizationService;
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
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid AuthenticationDTO data) {

        try {

            String token = authorizationService.login(data);

            return ResponseEntity.ok(
                    new LoginResponseDTO(token)
            );

        } catch (BadCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO(
                            401,
                            "Email ou senha inválidos"
                    ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegisterDTO data) {

        try {

            authorizationService.register(data);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ErrorResponseDTO(
                            201,
                            "Usuário registrado com sucesso"
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO(
                            409,
                            e.getMessage()
                    ));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        authorizationService.validateToken(authHeader);

        return ResponseEntity.ok("Token válido");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader) {

        String novoToken =
                authorizationService.refreshToken(authHeader);

        return ResponseEntity.ok(
                new LoginResponseDTO(novoToken)
        );
    }
}