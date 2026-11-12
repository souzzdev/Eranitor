package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.RelatorioDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.RelatorioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RelatorioController.class)
@AutoConfigureMockMvc(addFilters = false)
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RelatorioService relatorioService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void gerarRelatorioPeriodo_deveRetornarRelatorio() throws Exception {
        given(relatorioService.gerarRelatorioPeriodo(1L, java.time.LocalDate.of(2026, 10, 1), java.time.LocalDate.of(2026, 10, 31)))
                .willReturn(new RelatorioDTO(
                        "2026-10-01 a 2026-10-31",
                        List.of(),
                        List.of(),
                        List.of()
                ));

        mockMvc.perform(post("/relatorios/periodo/1")
                        .param("dataInicio", "2026-10-01")
                        .param("dataFim", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo").value("2026-10-01 a 2026-10-31"));
    }

    @Test
    void gerarRelatorioMateria_deveRetornarRelatorio() throws Exception {
        given(relatorioService.gerarRelatorioMateria(2L, java.time.LocalDate.of(2026, 10, 1), java.time.LocalDate.of(2026, 10, 31)))
                .willReturn(new RelatorioDTO(
                        "2026-10-01 a 2026-10-31",
                        List.of(),
                        List.of(),
                        List.of()
                ));

        mockMvc.perform(post("/relatorios/materia/2")
                        .param("mesInicio", "2026-10-01")
                        .param("mesFim", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo").value("2026-10-01 a 2026-10-31"));
    }

    @Test
    void gerarRelatorioTempoEstudo_deveRetornarRelatorio() throws Exception {
        given(relatorioService.gerarRelatorioTempoEstudo(1L, 10, 2026))
                .willReturn(new RelatorioDTO("10/2026", List.of(), List.of(), List.of()));

        mockMvc.perform(get("/relatorios/usuario/1/tempo/10/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo").value("10/2026"));
    }
}
