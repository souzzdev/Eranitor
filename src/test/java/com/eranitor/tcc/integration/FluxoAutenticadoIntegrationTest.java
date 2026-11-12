package com.eranitor.tcc.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração de ponta a ponta: contexto completo (filtros de
 * segurança reais, JWT real, H2 em memória).
 *
 * Objetivo principal: garantir que um POST autenticado funciona SEM
 * `.with(csrf())`. Se o CSRF voltar a ser habilitado, este teste falha.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FluxoAutenticadoIntegrationTest {

    private static final String SENHA = "Senha@123";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postAutenticadoSemCsrf_deveCriarMateria() throws Exception {
        Login login = registrarELogar();

        mockMvc.perform(post("/materias/usuario/{usuarioId}", login.idUsuario())
                        .header("Authorization", "Bearer " + login.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Matemática", "ativa": true, "dificuldade": 4}
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void postComTokenDeOutroUsuario_deveRetornar403ComCorpoJson() throws Exception {
        Login dono = registrarELogar();
        Login invasor = registrarELogar();

        mockMvc.perform(post("/materias/usuario/{usuarioId}", dono.idUsuario())
                        .header("Authorization", "Bearer " + invasor.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Física", "ativa": true, "dificuldade": 3}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void requisicaoSemToken_deveRetornar401ComCorpoJson() throws Exception {
        Login login = registrarELogar();

        mockMvc.perform(get("/materias/usuario/{usuarioId}", login.idUsuario()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void documentacaoOpenApi_deveEstarAcessivel() throws Exception {
        MvcResult resultado = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn();

        String corpo = resultado.getResponse().getContentAsString();

        assertFalse(corpo.isBlank(), "O JSON do OpenAPI veio vazio.");
        assertNotNull(JsonPath.read(corpo, "$.openapi"));
        assertEquals("ERANITOR API", JsonPath.read(corpo, "$.info.title"));

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteDeRecursoDeOutroUsuario_deveRetornar403() throws Exception {
        Login dono = registrarELogar();
        Login invasor = registrarELogar();

        mockMvc.perform(post("/materias/usuario/{usuarioId}", dono.idUsuario())
                        .header("Authorization", "Bearer " + dono.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome": "Química", "ativa": true, "dificuldade": 2}
                                """))
                .andExpect(status().isCreated());

        MvcResult lista = mockMvc.perform(get("/materias/usuario/{usuarioId}", dono.idUsuario())
                        .header("Authorization", "Bearer " + dono.token()))
                .andExpect(status().isOk())
                .andReturn();

        Number idMateria = JsonPath.read(
                lista.getResponse().getContentAsString(),
                "$[0].idMateria"
        );

        mockMvc.perform(delete("/materias/{id}", idMateria.longValue())
                        .header("Authorization", "Bearer " + invasor.token()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    private Login registrarELogar() throws Exception {
        String email = "teste-" + UUID.randomUUID() + "@eranitor.test";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s",
                                  "confirmPassword": "%s",
                                  "nome": "Usuário Teste",
                                  "instituicao": "ETEC",
                                  "serie": "3º ano"
                                }
                                """.formatted(email, SENHA, SENHA)))
                .andExpect(status().isCreated());

        MvcResult login = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "%s", "password": "%s"}
                                """.formatted(email, SENHA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String corpo = login.getResponse().getContentAsString();

        String token = JsonPath.read(corpo, "$.token");
        Number idUsuario = JsonPath.read(corpo, "$.usuario.idUsuario");

        return new Login(token, idUsuario.longValue());
    }

    private record Login(String token, Long idUsuario) {
    }
}
