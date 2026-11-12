package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.NotificacaoDTO;
import com.eranitor.tcc.dto.TarefaResponseDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.NotificacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificacaoController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacaoService notificacaoService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void gerarMensagemDiaria_deveRetornarNotificacao() throws Exception {
        given(notificacaoService.gerarMensagemDiaria(1L))
                .willReturn(new NotificacaoDTO("Você tem 2 tarefas pendentes", 50.0, List.of("Matemática")));

        mockMvc.perform(get("/notificacoes/dia/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Você tem 2 tarefas pendentes"))
                .andExpect(jsonPath("$.desempenho").value(50.0));
    }

    @Test
    void buscarTarefasProximas_deveRetornarLista() throws Exception {
        given(notificacaoService.buscarTarefasProximas(1L))
                .willReturn(List.of(new TarefaResponseDTO(
                        1L,
                        "Entregar projeto",
                        "Finalizar a documentação",
                        java.time.LocalDate.of(2026, 11, 20),
                        "#FF0000",
                        false,
                        1L
                )));

        mockMvc.perform(get("/notificacoes/tarefas-proximas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idTarefa").value(1))
                .andExpect(jsonPath("$[0].materiaId").value(1));
    }

    @Test
    void buscarDesempenhoPercentual_deveRetornarDouble() throws Exception {
        given(notificacaoService.buscarDesempenhoPercentual(1L)).willReturn(75.0);

        mockMvc.perform(get("/notificacoes/desempenho/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(75.0));
    }
}
