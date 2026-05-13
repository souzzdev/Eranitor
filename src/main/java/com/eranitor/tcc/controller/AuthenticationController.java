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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepositoy repositoy;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senhaHash());
        var auth = this.authenticationManager.authenticate(userNamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register (@RequestBody @Valid RegisterDTO data) {
        try {
            if(this.repositoy.findByLogin(data.email()) != null) return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(
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
}
