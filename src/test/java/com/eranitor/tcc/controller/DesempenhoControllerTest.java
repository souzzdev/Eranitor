package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.DesempenhoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DesempenhoController.class)
@AutoConfigureMockMvc(addFilters = false)
class DesempenhoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DesempenhoService desempenhoService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void registrarDesempenho_deveRetornar201() throws Exception {
        given(desempenhoService.registrarDesempenho(1L, 1L, 10, 2026, 2))
                .willReturn(new DesempenhoDTO(1L, 1L, 1L, 10, 2026, 2, 5));

        mockMvc.perform(post("/desempenho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "usuarioId": 1,
                                    "materiaId": 1,
                                    "mes": 10,
                                    "ano": 2026,
                                    "horasConcluidas": 2,
                                    "horasMeta": 5
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void registrarDesempenho_deveRetornar409QuandoHorasExcedemMeta() throws Exception {
        doThrow(new RuntimeException("Horas concluídas excedem a meta semanal."))
                .when(desempenhoService).registrarDesempenho(1L, 1L, 10, 2026, 2);

        mockMvc.perform(post("/desempenho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "usuarioId": 1,
                                    "materiaId": 1,
                                    "mes": 10,
                                    "ano": 2026,
                                    "horasConcluidas": 2,
                                    "horasMeta": 5
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Horas concluídas excedem a meta semanal."));
    }

    @Test
    void calcularDesempenhoPorPeriodo_deveRetornar200() throws Exception {
        given(desempenhoService.calcularDesempenho(1L, 1L, 10, 2026))
                .willReturn(new DesempenhoDTO(1L, 1L, 1L, 10, 2026, 2, 5));

        mockMvc.perform(get("/desempenho/usuario/1/materia/1/periodo/10/2026"))
                .andExpect(status().isOk());
    }

    @Test
    void getDesempenhoUsuario_deveRetornarLista() throws Exception {
        given(desempenhoService.getDesempenhoUsuario(1L, 10, 2026))
                .willReturn(List.of(new DesempenhoDTO(1L, 1L, 1L, 10, 2026, 2, 5)));

        mockMvc.perform(get("/desempenho/usuario/1/periodo/10/2026"))
                .andExpect(status().isOk());
    }
}
