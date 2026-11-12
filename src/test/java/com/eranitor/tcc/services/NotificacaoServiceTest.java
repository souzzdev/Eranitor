package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.NotificacaoDTO;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProgressoService progressoService;

    @Mock
    private PriorizacaoService priorizacaoService;

    @InjectMocks
    private NotificacaoService service;

    @Test
    void gerarMensagemDiaria_deveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.gerarMensagemDiaria(1L));
    }

    @Test
    void gerarMensagemDiaria_deveRetornarMensagemComDesempenhoEDesTarefas() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(progressoService.calcularProgressoGeral(1L)).thenReturn(50.0);

        Tarefa tarefa1 = new Tarefa();
        tarefa1.setTitulo("Matemática");

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setTitulo("História");

        when(priorizacaoService.getTarefasProximas(1L, 3)).thenReturn(List.of(tarefa1, tarefa2));

        NotificacaoDTO dto = service.gerarMensagemDiaria(1L);

        assertNotNull(dto);
        assertEquals(50.0, dto.desempenho(), 0.0);
        assertEquals(List.of("Matemática", "História"), dto.tarefasProximas());
        assertTrue(dto.mensagem().contains("Matemática"));
    }

    @Test
    void buscarDesempenhoPercentual_deveRetornarProgressoDoUsuario() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(progressoService.calcularProgressoGeral(1L)).thenReturn(75.5);

        assertEquals(75.5, service.buscarDesempenhoPercentual(1L), 0.0);
    }

    @Test
    void construirMensagem_semTarefasDeveRetornarTextoPadrao() {
        String mensagem = service.construirMensagem(100.0, List.of());

        assertNotNull(mensagem);
        assertTrue(mensagem.contains("sem tarefas próximas"));
    }

    @Test
    void construirMensagem_comTarefasDeveConcatenarNomesDasTarefas() {
        String mensagem = service.construirMensagem(100.0, List.of("Matemática", "Física"));

        assertNotNull(mensagem);
        assertTrue(mensagem.contains("Matemática, Física"));
    }
}
