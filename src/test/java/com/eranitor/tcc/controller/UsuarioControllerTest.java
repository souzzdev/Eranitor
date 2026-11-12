package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.AlterarEmailDTO;
import com.eranitor.tcc.dto.AlterarSenhaDTO;
import com.eranitor.tcc.dto.AtualizarPerfilDTO;
import com.eranitor.tcc.dto.UsuarioResponseDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.ResponseEntity;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void findByLogin_deveRetornar200() throws Exception {
        given(usuarioService.findByLogin("guilherme@exemplo.com"))
                .willAnswer((Answer<ResponseEntity<?>>) invocation -> ResponseEntity.ok(
                        new UsuarioResponseDTO(1L, "Guilherme", "guilherme@exemplo.com", "1234", "ETEC", null, null)
                ));

        mockMvc.perform(get("/usuarios/login/guilherme@exemplo.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1L));
    }

    @Test
    void findByLogin_deveRetornar404() throws Exception {
        given(usuarioService.findByLogin("ninguem@exemplo.com"))
                .willAnswer((Answer<ResponseEntity<?>>) invocation -> ResponseEntity.status(404).build());

        mockMvc.perform(get("/usuarios/login/ninguem@exemplo.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPerfil_deveRetornar200() throws Exception {
        given(usuarioService.getPerfil("guilherme@exemplo.com"))
                .willReturn(new UsuarioResponseDTO(1L, "Guilherme", "guilherme@exemplo.com", "1234", "ETEC", null, null));

        mockMvc.perform(get("/usuarios/perfil").param("email", "guilherme@exemplo.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("guilherme@exemplo.com"));
    }

    @Test
    void updatePerfil_deveRetornar200() throws Exception {
        given(usuarioService.updatePerfil(eq("guilherme@exemplo.com"), any(AtualizarPerfilDTO.class)))
                .willReturn(new UsuarioResponseDTO(1L, "Guilherme", "guilherme@exemplo.com", "1234", "ETEC", null, null));

        mockMvc.perform(put("/usuarios/perfil")
                        .param("email", "guilherme@exemplo.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Guilherme\",\"instituicao\":\"ETEC\",\"serie\":\"1234\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void alterarEmail_deveRetornar204() throws Exception {
        doNothing().when(usuarioService).alterarEmail(eq("guilherme@exemplo.com"), any(AlterarEmailDTO.class));

        mockMvc.perform(post("/usuarios/email")
                        .param("emailAtual", "guilherme@exemplo.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"novoEmail\":\"novo@exemplo.com\",\"senhaAtual\":\"SenhaForte@123\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void alterarEmail_deveRetornar400QuandoSenhaIncorreta() throws Exception {
        doThrow(new IllegalArgumentException("Senha incorreta."))
                .when(usuarioService).alterarEmail(eq("guilherme@exemplo.com"), any(AlterarEmailDTO.class));

        mockMvc.perform(post("/usuarios/email")
                        .param("emailAtual", "guilherme@exemplo.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"novoEmail\":\"novo@exemplo.com\",\"senhaAtual\":\"SenhaForte@123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Senha incorreta."));
    }

    @Test
    void alterarSenha_deveRetornar204() throws Exception {
        doNothing().when(usuarioService).alterarSenha(eq(1L), any(AlterarSenhaDTO.class));

        mockMvc.perform(post("/usuarios/1/senha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"SenhaForte@123\",\"novaSenha\":\"NovaSenha@123\",\"confirmarSenha\":\"NovaSenha@123\"}"))
                .andExpect(status().isNoContent());
    }
}
