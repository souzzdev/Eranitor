package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.CreateTopicoDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.TopicoDTO;
import com.eranitor.tcc.dto.TopicoResponseDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.TopicoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TopicoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TopicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TopicoService topicoService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void criarTopico_deveRetornar201() throws Exception {
        mockMvc.perform(post("/topicos/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Top\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void criarTopico_deveRetornar400QuandoTopicoDuplicado() throws Exception {
        doThrow(new IllegalArgumentException("Tópico já existe."))
                .when(topicoService).cadastrarTopico(any(CreateTopicoDTO.class), eq(1L));

        mockMvc.perform(post("/topicos/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Top\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tópico já existe."));
    }

    @Test
    void updateTopico_deveRetornar200() throws Exception {
        mockMvc.perform(put("/topicos/1/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Novo\",\"concluido\":false}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTopico_deveRetornar404() throws Exception {
        doThrow(new RuntimeException("Tópico não encontrado."))
                .when(topicoService).deleteTopico(1L);

        mockMvc.perform(delete("/topicos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void marcarConcluido_deveRetornar200() throws Exception {
        mockMvc.perform(post("/topicos/1/concluir"))
                .andExpect(status().isOk());
    }

    @Test
    void listarTopicos_deveRetornarLista() throws Exception {
        given(topicoService.getTopicosByMateria(1L))
                .willReturn(java.util.List.of(new TopicoResponseDTO(1L, "Top", false)));

        mockMvc.perform(get("/topicos/materia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTopico").value(1))
                .andExpect(jsonPath("$[0].nome").value("Top"))
                .andExpect(jsonPath("$[0].concluido").value(false));
    }
}
