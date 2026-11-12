package com.eranitor.tcc.integration;

import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.enums.UsuarioRole;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Queda de banco não pode virar 401 nem 500:
 * - falha ao resolver o usuário do token no SecurityFilter → 503;
 * - falha dentro do controller/service → 503 via GlobalExceptionHandler.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IndisponibilidadeBancoIntegrationTest {

    private static final String EMAIL = "indisponivel@eranitor.test";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private MateriaRepository materiaRepository;

    @Test
    void falhaNoFiltroAoResolverUsuario_deveRetornar503() throws Exception {
        given(usuarioRepository.findByEmail(anyString()))
                .willThrow(new DataAccessResourceFailureException("pool esgotado"));

        mockMvc.perform(get("/materias/usuario/{id}", 1L)
                        .header("Authorization", "Bearer " + tokenValido()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Retry-After", "5"))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void falhaDeBancoDentroDoController_deveRetornar503() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail(EMAIL);
        usuario.setRole(UsuarioRole.USER);

        given(usuarioRepository.findByEmail(EMAIL)).willReturn(java.util.Optional.of(usuario));
        given(materiaRepository.findAllByUsuario_IdUsuario(1L))
                .willThrow(new DataAccessResourceFailureException("conexão recusada"));

        mockMvc.perform(get("/materias/usuario/{id}", 1L)
                        .header("Authorization", "Bearer " + tokenValido()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Retry-After", "5"))
                .andExpect(jsonPath("$.status").value(503));
    }

    private String tokenValido() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail(EMAIL);
        usuario.setRole(UsuarioRole.USER);

        return tokenService.generateToken(usuario);
    }
}
