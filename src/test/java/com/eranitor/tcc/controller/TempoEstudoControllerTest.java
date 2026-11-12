package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.TempoEstudoDTO;
import com.eranitor.tcc.dto.TempoEstudoRegistroDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.TempoEstudoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TempoEstudoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TempoEstudoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TempoEstudoService tempoEstudoService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void registrarTempo_deveRetornar201() throws Exception {
        given(tempoEstudoService.registrarTempo(any(TempoEstudoRegistroDTO.class), eq(1L)))
                .willReturn(new TempoEstudoDTO(1, 1L, 1L, 10, 2026, 60, 0, 0));

        mockMvc.perform(post("/tempos-estudo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "tipo": "ESTUDO",
                                    "minutos": 60,
                                    "materiaId": 1
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void registrarTempo_deveRetornar400QuandoDadosInvalidos() throws Exception {
        doThrow(new IllegalArgumentException("O tipo de atividade é obrigatório."))
                .when(tempoEstudoService).registrarTempo(any(TempoEstudoRegistroDTO.class), eq(1L));

        mockMvc.perform(post("/tempos-estudo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "tipo": "ESTUDO",
                                    "minutos": 60,
                                    "materiaId": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O tipo de atividade é obrigatório."));
    }

    @Test
    void listarPorUsuario_deveRetornarLista() throws Exception {
        given(tempoEstudoService.listarPorUsuario(1L))
                .willReturn(List.of(new TempoEstudoDTO(1, 1L, 1L, 10, 2026, 60, 0, 0)));

        mockMvc.perform(get("/tempos-estudo/usuario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void listarPorMateria_deveRetornarLista() throws Exception {
        given(tempoEstudoService.listarPorTarefa(1L))
                .willReturn(List.of(new TempoEstudoDTO(1, 1L, 1L, 10, 2026, 60, 0, 0)));

        mockMvc.perform(get("/tempos-estudo/materia/1"))
                .andExpect(status().isOk());
    }
}
