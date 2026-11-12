package com.eranitor.tcc.controller;

import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.ProgressoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgressoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgressoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProgressoService progressoService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void getProgressoGeral_deveRetornar200() throws Exception {
        given(progressoService.calcularProgressoGeral(1L)).willReturn(0.75);

        mockMvc.perform(get("/progresso/usuario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getProgressoMateria_deveRetornar200() throws Exception {
        given(progressoService.calcularProgressoMateria(1L)).willReturn(0.5);

        mockMvc.perform(get("/progresso/materia/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getProgressoPorMateria_deveRetornar200() throws Exception {
        given(progressoService.getProgressoPorMateria(1L))
                .willReturn(Map.of(1L, 0.5));

        mockMvc.perform(get("/progresso/usuario/1/materias"))
                .andExpect(status().isOk());
    }
}
