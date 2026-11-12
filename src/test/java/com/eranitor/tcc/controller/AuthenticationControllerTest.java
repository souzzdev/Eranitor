package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.AuthenticationDTO;
import com.eranitor.tcc.dto.LoginResponseDTO;
import com.eranitor.tcc.dto.RegisterDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.AuthorizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorizationService authorizationService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void login_deveRetornarTokenQuandoCredenciaisSaoValidas() throws Exception {
        given(authorizationService.login(any(AuthenticationDTO.class)))
                .willReturn(new LoginResponseDTO("token-abc", null));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@exemplo.com\",\"password\":\"SenhaForte@123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-abc"));
    }

    @Test
    void login_deveRetornar401QuandoCredenciaisSaoInvalidas() throws Exception {
        given(authorizationService.login(any(AuthenticationDTO.class)))
                .willThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"teste@exemplo.com\",\"password\":\"SenhaErrada@123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Email ou senha inválidos"));
    }

    @Test
    void register_deveRetornar201QuandoUsuarioCriado() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "teste@exemplo.com",
                                    "password": "SenhaForte@123",
                                    "confirmPassword": "SenhaForte@123",
                                    "nome": "Guilherme",
                                    "instituicao": "ETEC",
                                    "serie": "1234"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201));
    }

    @Test
    void register_deveRetornar409QuandoEmailJaCadastrado() throws Exception {
        doThrow(new IllegalArgumentException("Email já cadastrado!"))
                .when(authorizationService)
                .register(any(RegisterDTO.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "email": "teste@exemplo.com",
                                    "password": "SenhaForte@123",
                                    "confirmPassword": "SenhaForte@123",
                                    "nome": "Guilherme",
                                    "instituicao": "ETEC",
                                    "serie": "1234"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email já cadastrado!"));
    }

    @Test
    void validate_deveRetornar200QuandoTokenValido() throws Exception {
        mockMvc.perform(get("/auth/validate")
                        .header("Authorization", "Bearer token-abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("Token válido"));
    }

    @Test
    void refresh_deveRetornarNovoToken() throws Exception {
        given(authorizationService.refreshToken("Bearer token-abc"))
                .willReturn(new LoginResponseDTO("novo-token", "novo-refresh", null));

        mockMvc.perform(post("/auth/refresh")
                        .header("Authorization", "Bearer token-abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("novo-token"))
                .andExpect(jsonPath("$.refreshToken").value("novo-refresh"));
    }
}
