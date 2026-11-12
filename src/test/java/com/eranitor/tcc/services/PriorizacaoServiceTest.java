package com.eranitor.tcc.services;

import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriorizacaoServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private ProgressoService progressoService;

    @InjectMocks
    private PriorizacaoService service;

    @Test
    void calcularAtraso_deveRetornarZeroQuandoConcluidaOuSemData() {
        assertEquals(0.0, service.calcularAtraso(null, false), 0.0);
        assertEquals(0.0, service.calcularAtraso(LocalDate.now(), true), 0.0);
    }

    @Test
    void calcularAtraso_deveRetornarDiasDeAtraso() {
        assertEquals(1.0, service.calcularAtraso(LocalDate.now().minusDays(1), false), 0.0);
        assertEquals(0.0, service.calcularAtraso(LocalDate.now(), false), 0.0);
    }

    @Test
    void calcularProximidade_deveRetornarValoresDeUrgencia() {
        assertEquals(0.0, service.calcularProximidade(null), 0.0);
        assertEquals(3.0, service.calcularProximidade(LocalDate.now()), 0.0);
        assertEquals(2.0, service.calcularProximidade(LocalDate.now().plusDays(5)), 0.0);
        assertEquals(1.0, service.calcularProximidade(LocalDate.now().plusDays(30)), 0.0);
    }

    @Test
    void calcularPrioridade_deveLancarQuandoProgressoForaDoRange() {
        Tarefa tarefa = new Tarefa();
        Materia materia = new Materia();
        materia.setDificuldade(2);
        tarefa.setMateria(materia);

        assertThrows(IllegalArgumentException.class, () -> service.calcularPrioridade(tarefa, 1.5));
    }

    @Test
    void calcularPrioridade_deveRetornarPrioridadeCalculada() {
        Tarefa tarefa = new Tarefa();
        tarefa.setConcluida(false);
        tarefa.setDataVencimento(LocalDate.now());
        Materia materia = new Materia();
        materia.setDificuldade(2);
        tarefa.setMateria(materia);

        double prioridade = service.calcularPrioridade(tarefa, 0.5);

        assertEquals(10.0, prioridade, 0.0);
    }

    @Test
    void getTarefasProximas_deveLancarQuandoParametrosInvalidos() {
        assertThrows(NullPointerException.class, () -> service.getTarefasProximas(null, 3));
        assertThrows(IllegalArgumentException.class, () -> service.getTarefasProximas(1L, -1));
    }

    @Test
    void getTarefasProximas_deveRetornarTarefasDoPeriodo() {
        Tarefa tarefa1 = new Tarefa();
        tarefa1.setTitulo("Matemática");
        tarefa1.setConcluida(false);
        tarefa1.setDataVencimento(LocalDate.now());

        Tarefa tarefa2 = new Tarefa();
        tarefa2.setTitulo("História");
        tarefa2.setConcluida(false);
        tarefa2.setDataVencimento(LocalDate.now().plusDays(10));

        when(tarefaRepository.findByUsuarioIdUsuario(1L)).thenReturn(List.of(tarefa1, tarefa2));

        List<Tarefa> proximas = service.getTarefasProximas(1L, 5);

        assertEquals(1, proximas.size());
        assertEquals("Matemática", proximas.get(0).getTitulo());
    }

    @Test
    void getTarefasProximas_deveRetornarListaVaziaQuandoNaoHaProximas() {
        when(tarefaRepository.findByUsuarioIdUsuario(1L)).thenReturn(Collections.emptyList());

        List<Tarefa> proximas = service.getTarefasProximas(1L, 3);

        assertNotNull(proximas);
        assertTrue(proximas.isEmpty());
    }
}
