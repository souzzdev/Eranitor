package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.AgendaCreateDTO;
import com.eranitor.tcc.dto.AgendaDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.AgendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AgendaController.class)
@AutoConfigureMockMvc(addFilters = false)
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgendaService agendaService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void criarAgenda_deveRetornar201() throws Exception {
        given(agendaService.criarAgenda(any(AgendaCreateDTO.class), eq(1L)))
                .willReturn(new AgendaDTO(1, 1L, LocalDate.now(), "Revisão", "ESTUDO"));

        mockMvc.perform(post("/agenda/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"2026-10-10\",\"titulo\":\"Revisão\",\"tipo\":\"ESTUDO\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void criarAgenda_deveRetornar400QuandoTituloInvalido() throws Exception {
        given(agendaService.criarAgenda(any(AgendaCreateDTO.class), eq(1L)))
                .willThrow(new IllegalArgumentException("O título é obrigatório"));

        mockMvc.perform(post("/agenda/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"2026-10-10\",\"titulo\":\"  \",\"tipo\":\"ESTUDO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void listarAgenda_deveRetornarLista() throws Exception {
        given(agendaService.listarPorUsuario(1L))
                .willReturn(List.of(new AgendaDTO(1, 1L, LocalDate.now(), "Revisão", "ESTUDO")));

        mockMvc.perform(get("/agenda/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Revisão"));
    }

    @Test
    void atualizarAgenda_deveRetornar200() throws Exception {
        given(agendaService.atualizarAgenda(any(Integer.class), any(AgendaCreateDTO.class)))
                .willReturn(new AgendaDTO(1, 1L, LocalDate.now(), "Revisão", "ESTUDO"));

        mockMvc.perform(put("/agenda/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"data\":\"2026-10-10\",\"titulo\":\"Revisão\",\"tipo\":\"ESTUDO\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deletarAgenda_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/agenda/1"))
                .andExpect(status().isNoContent());
    }
}
