package com.eranitor.tcc.services;

import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressoServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private DesempenhoRepository desempenhoRepository;

    @InjectMocks
    private ProgressoService service;

    @Test
    void calcularProgressoGeral_deveRetornarZeroQuandoNaoHaTarefas() {
        when(tarefaRepository.countByUsuarioIdUsuario(1L)).thenReturn(0L);

        assertEquals(0.0, service.calcularProgressoGeral(1L), 0.0);
    }

    @Test
    void calcularProgressoGeral_deveRetornarPercentualDeConcluidas() {
        when(tarefaRepository.countByUsuarioIdUsuario(1L)).thenReturn(2L);
        when(tarefaRepository.countByUsuarioIdUsuarioAndConcluida(1L, true)).thenReturn(1L);

        assertEquals(50.0, service.calcularProgressoGeral(1L), 0.0);
    }

    @Test
    void calcularProgressoGeral_deveLancarQuandoUsuarioIdNulo() {
        assertThrows(NullPointerException.class, () -> service.calcularProgressoGeral(null));
    }

    @Test
    void calcularProgressoMateria_deveRetornarZeroQuandoNaoHaTarefas() {
        when(tarefaRepository.countByMateriaIdMateria(1L)).thenReturn(0L);

        assertEquals(0.0, service.calcularProgressoMateria(1L), 0.0);
    }

    @Test
    void calcularProgressoMateria_deveRetornarPercentualDeConcluidas() {
        when(tarefaRepository.countByMateriaIdMateria(1L)).thenReturn(4L);
        when(tarefaRepository.countByMateriaIdMateriaAndConcluida(1L, true)).thenReturn(1L);

        assertEquals(25.0, service.calcularProgressoMateria(1L), 0.0);
    }

    @Test
    void getProgressoPorMateria_deveRetornarProgressoDeCadaMateria() {
        Materia materia = new Materia();
        materia.setIdMateria(1L);
        materia.setNome("Matemática");

        Tarefa tarefa = new Tarefa();
        tarefa.setMateria(materia);
        tarefa.setConcluida(true);

        when(materiaRepository.findAllByUsuario_IdUsuario(1L)).thenReturn(List.of(materia));
        when(tarefaRepository.findByUsuarioIdUsuario(1L)).thenReturn(List.of(tarefa));

        Map<Long, Double> progresso = service.getProgressoPorMateria(1L);

        assertEquals(1, progresso.size());
        assertEquals(100.0, progresso.get(1L), 0.0);
    }

    @Test
    void getProgressoPorMateria_deveRetornarMapaVazioQuandoNaoHaTarefas() {
        when(materiaRepository.findAllByUsuario_IdUsuario(1L)).thenReturn(Collections.emptyList());
        when(tarefaRepository.findByUsuarioIdUsuario(1L)).thenReturn(Collections.emptyList());

        Map<Long, Double> progresso = service.getProgressoPorMateria(1L);

        assertNotNull(progresso);
        assertTrue(progresso.isEmpty());
    }

    @Test
    void getProgressoTempoMateria_deveRetornarZeroQuandoHorasMetaInvalidas() {
        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of());

        assertEquals(0.0, service.getProgressoTempoMateria(1L, 10, 2026), 0.0);
    }

    @Test
    void getProgressoTempoMateria_deveRetornarPercentualDeHoras() {
        Desempenho desempenho = new Desempenho();
        desempenho.setHorasConcluidas(2);
        desempenho.setHorasMeta(4);

        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of(desempenho));

        assertEquals(50.0, service.getProgressoTempoMateria(1L, 10, 2026), 0.0);
    }

    @Test
    void getProgressoTempoMateria_deveValidarMesECurso() {
        assertThrows(IllegalArgumentException.class, () -> service.getProgressoTempoMateria(1L, 13, 2026));
        assertThrows(IllegalArgumentException.class, () -> service.getProgressoTempoMateria(1L, 10, 0));
    }
}
