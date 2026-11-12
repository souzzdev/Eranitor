package com.eranitor.tcc.controller;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.MetaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MetaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MetaService metaService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void setMetaSemanal_deveRetornar200() throws Exception {
        mockMvc.perform(put("/meta/semana/1")
                        .param("horas", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void setMetaSemanal_deveRetornar400QuandoMetaInvalida() throws Exception {
        doThrow(new IllegalArgumentException("A meta semanal deve estar entre 1 e 168 horas."))
                .when(metaService).setMetaSemanal(1L, 0);

        mockMvc.perform(put("/meta/semana/1")
                        .param("horas", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("A meta semanal deve estar entre 1 e 168 horas."));
    }

    @Test
    void setMetaMateria_deveRetornar200() throws Exception {
        mockMvc.perform(put("/meta/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "materiaId": 1,
                                    "mes": 10,
                                    "ano": 2026,
                                    "horasMeta": 5
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void setMetaMateria_deveRetornar400QuandoHorasInvalidas() throws Exception {
        doThrow(new RuntimeException("As horas de meta devem ser maiores que zero"))
                .when(metaService).setMetaMateria(1L, 1L, 10, 2026, 5);

        mockMvc.perform(put("/meta/materia/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "materiaId": 1,
                                    "mes": 10,
                                    "ano": 2026,
                                    "horasMeta": 5
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
