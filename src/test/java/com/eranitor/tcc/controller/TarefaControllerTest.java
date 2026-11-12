package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.TarefaDTO;
import com.eranitor.tcc.dto.TarefaPriorizadaDTO;
import com.eranitor.tcc.dto.TarefaResponseDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.TarefaService;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TarefaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TarefaService tarefaService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void getTarefasPriorizadas_deveRetornarLista() throws Exception {
        given(tarefaService.getTarefasOrdenadas(1L)).willReturn(List.of(new TarefaPriorizadaDTO(
                1L,
                "Entregar projeto",
                "Finalizar a documentação",
                LocalDate.now(),
                "#FF0000",
                false,
                0.8,
                1L,
                "Matemática",
                1L
        )));

        mockMvc.perform(get("/tarefas/priorizadas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Entregar projeto"));
    }

    @Test
    void criarTarefa_deveRetornar201() throws Exception {
        mockMvc.perform(post("/tarefas/1/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Entregar projeto",
                                    "descricao": "Finalizar a documentação",
                                    "dataVencimento": "2026-11-20",
                                    "cor": "#FF0000"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void criarTarefa_deveRetornar409QuandoTituloDuplicado() throws Exception {
        doThrow(new RuntimeException("Você já possui uma tarefa com esse título nessa matéria."))
                .when(tarefaService).createTarefa(eq(1L), eq(1L), any(TarefaDTO.class));

        mockMvc.perform(post("/tarefas/1/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Entregar projeto",
                                    "descricao": "Finalizar a documentação",
                                    "dataVencimento": "2026-11-20",
                                    "cor": "#FF0000"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Você já possui uma tarefa com esse título nessa matéria."));
    }

    @Test
    void atualizarTarefa_deveRetornar200() throws Exception {
        mockMvc.perform(put("/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Entregar projeto",
                                    "descricao": "Finalizar a documentação",
                                    "dataVencimento": "2026-11-20",
                                    "cor": "#FF0000"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void atualizarTarefa_deveRetornar404QuandoNaoEncontrada() throws Exception {
        doThrow(new IllegalArgumentException("Tarefa não encontrada."))
                .when(tarefaService).updateTarefa(eq(1L), any(TarefaDTO.class));

        mockMvc.perform(put("/tarefas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Entregar projeto",
                                    "descricao": "Finalizar a documentação",
                                    "dataVencimento": "2026-11-20",
                                    "cor": "#FF0000"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void concluirTarefa_deveRetornar200SemBody() throws Exception {
        mockMvc.perform(post("/tarefas/1/concluir"))
                .andExpect(status().isOk());
    }

    @Test
    void reabrirTarefa_deveRetornar200SemBody() throws Exception {
        mockMvc.perform(post("/tarefas/1/reabrir"))
                .andExpect(status().isOk());
    }

    @Test
    void deletarTarefa_deveRetornar204() throws Exception {
        mockMvc.perform(delete("/tarefas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletarTarefa_deveRetornar404QuandoNaoEncontrada() throws Exception {
        doThrow(new RuntimeException("Tarefa não encontrada"))
                .when(tarefaService).deleteTarefa(1L);

        mockMvc.perform(delete("/tarefas/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void listarTarefasPorMateria_deveRetornarLista() throws Exception {
        given(tarefaService.getTarefasByMateria(1L))
                .willReturn(List.of(new TarefaResponseDTO(
                        1L,
                        "Entregar projeto",
                        "Finalizar a documentação",
                        LocalDate.of(2026, 11, 20),
                        "#FF0000",
                        false,
                        1L
                )));

        mockMvc.perform(get("/tarefas/materia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTarefa").value(1))
                .andExpect(jsonPath("$[0].concluida").value(false))
                .andExpect(jsonPath("$[0].materiaId").value(1));
    }

    @Test
    void listarTarefasPorUsuario_deveRetornarLista() throws Exception {
        given(tarefaService.getTarefasByUsuario(1L))
                .willReturn(List.of(new TarefaResponseDTO(
                        1L,
                        "Entregar projeto",
                        "Finalizar a documentação",
                        LocalDate.of(2026, 11, 20),
                        "#FF0000",
                        true,
                        1L
                )));

        mockMvc.perform(get("/tarefas/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].concluida").value(true));
    }
}
