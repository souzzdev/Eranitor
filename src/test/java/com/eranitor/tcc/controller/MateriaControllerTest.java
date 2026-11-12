package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.dto.MateriaDTO;
import com.eranitor.tcc.dto.MateriaResponseDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.MateriaService;
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

@WebMvcTest(controllers = MateriaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MateriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MateriaService materiaService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void criarMateria_deveRetornar201() throws Exception {
        mockMvc.perform(post("/materias/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Matemática\",\"ativa\":true,\"dificuldade\":2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void criarMateria_deveRetornar400QuandoMatériaDuplicada() throws Exception {
        doThrow(new IllegalArgumentException("Você já possui uma matéria com esse nome."))
                .when(materiaService).cadastrarMateria(any(MateriaDTO.class), eq(1L));

        mockMvc.perform(post("/materias/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Matemática\",\"ativa\":true,\"dificuldade\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Você já possui uma matéria com esse nome."));
    }

    @Test
    void updateMateria_deveRetornar200() throws Exception {
        given(materiaService.updateMateria(any(MateriaDTO.class), eq(1L), eq(1L)))
                .willReturn(new MateriaResponseDTO(1L, "Matemática", true, 2));

        mockMvc.perform(put("/materias/1/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Matemática\",\"ativa\":true,\"dificuldade\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Matemática"));
    }

    @Test
    void deletarMateria_deveRetornar404() throws Exception {
        doThrow(new RuntimeException("Matéria não encontrada!"))
                .when(materiaService).deleteMateria(1L);

        mockMvc.perform(delete("/materias/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarMaterias_deveRetornarLista() throws Exception {
        given(materiaService.getTodasMaterias(1L))
                .willReturn(java.util.List.of(new MateriaResponseDTO(1L, "Matemática", true, 3)));

        mockMvc.perform(get("/materias/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMateria").value(1))
                .andExpect(jsonPath("$[0].nome").value("Matemática"))
                .andExpect(jsonPath("$[0].ativa").value(true))
                .andExpect(jsonPath("$[0].dificuldade").value(3));
    }

    @Test
    void listarMateriasAtivas_deveRetornarLista() throws Exception {
        given(materiaService.getMateriasAtivas(1L))
                .willReturn(java.util.List.of(new MateriaResponseDTO(1L, "Matemática", true, 3)));

        mockMvc.perform(get("/materias/usuario/1/ativas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMateria").value(1));
    }
}
