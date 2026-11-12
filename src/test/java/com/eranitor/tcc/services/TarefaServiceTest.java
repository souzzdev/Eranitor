package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.TarefaDTO;
import com.eranitor.tcc.dto.TarefaPriorizadaDTO;
import com.eranitor.tcc.dto.TarefaResponseDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TarefaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private PriorizacaoService priorizacaoService;

    @InjectMocks
    private TarefaService service;

    @Test
    void getTarefasOrdenadas_deveRetornarTarefasPriorizadas() {
        Tarefa tarefa = new Tarefa();
        tarefa.setIdTarefa(1L);
        tarefa.setTitulo("Matemática");
        tarefa.setDescricao("Cap. 5");
        tarefa.setDataVencimento(LocalDate.now());
        tarefa.setCor("#FF0000");
        tarefa.setConcluida(false);

        Materia materia = new Materia();
        materia.setIdMateria(2L);
        materia.setNome("Matemática");
        tarefa.setMateria(materia);

        when(priorizacaoService.getTarefasOrdenadas(1L)).thenReturn(List.of(tarefa));
        when(priorizacaoService.calcularPrioridade(tarefa, 0.0)).thenReturn(10.0);

        List<TarefaPriorizadaDTO> tarefas = service.getTarefasOrdenadas(1L);

        assertEquals(1, tarefas.size());
        assertEquals(1L, tarefas.get(0).idTarefa());
        assertEquals(10.0, tarefas.get(0).prioridadeCalculada(), 0.0);
        assertEquals(2L, tarefas.get(0).materiaId());
        assertEquals("Matemática", tarefas.get(0).nomeMateria());
    }

    @Test
    void createTarefa_deveLancarQuandoTituloDuplicado() {
        when(tarefaRepository.existsByTituloIgnoreCaseAndMateria_IdMateriaAndUsuario_IdUsuario("X", 1L, 1L))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                service.createTarefa(1L, 1L, new TarefaDTO("X", "desc", LocalDate.now(), "#FF0000")));
    }

    @Test
    void createTarefa_deveLancarQuandoUsuarioNaoExiste() {
        when(tarefaRepository.existsByTituloIgnoreCaseAndMateria_IdMateriaAndUsuario_IdUsuario("X", 1L, 1L))
                .thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.createTarefa(1L, 1L, new TarefaDTO("X", "desc", LocalDate.now(), "#FF0000")));
    }

    @Test
    void createTarefa_deveSalvarTarefa() {
        when(tarefaRepository.existsByTituloIgnoreCaseAndMateria_IdMateriaAndUsuario_IdUsuario("X", 1L, 1L))
                .thenReturn(false);
        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(new com.eranitor.tcc.entity.Usuario()));
        when(materiaRepository.findById(1L)).thenReturn(java.util.Optional.of(new Materia()));

        service.createTarefa(1L, 1L, new TarefaDTO("X", "desc", LocalDate.now(), "#FF0000"));

        verify(tarefaRepository).save(any(Tarefa.class));
    }

    @Test
    void updateTarefa_deveLancarQuandoTarefaNaoEncontrada() {
        when(tarefaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.updateTarefa(1L, new TarefaDTO("Novo", "desc", LocalDate.now(), "#FF0000")));
    }

    @Test
    void updateTarefa_deveAtualizarTarefa() {
        Tarefa tarefa = new Tarefa();
        tarefa.setIdTarefa(1L);
        tarefa.setTitulo("X");

        when(tarefaRepository.findById(1L)).thenReturn(java.util.Optional.of(tarefa));

        service.updateTarefa(1L, new TarefaDTO("Novo", "desc", LocalDate.now(), "#FF0000"));

        assertEquals("Novo", tarefa.getTitulo());
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void updateTarefa_deveTrocarMateriaQuandoMateriaIdInformado() {
        Tarefa tarefa = new Tarefa();
        tarefa.setIdTarefa(1L);
        tarefa.setTitulo("X");

        Materia novaMateria = new Materia();
        novaMateria.setIdMateria(2L);

        when(tarefaRepository.findById(1L)).thenReturn(java.util.Optional.of(tarefa));
        when(materiaRepository.findById(2L)).thenReturn(java.util.Optional.of(novaMateria));

        service.updateTarefa(1L, new TarefaDTO("Novo", "desc", LocalDate.now(), "#FF0000", 2L));

        assertEquals(2L, tarefa.getMateria().getIdMateria());
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void getTarefasByUsuario_deveRetornarTodasAsTarefasIncluindoConcluidas() {
        Tarefa concluida = new Tarefa();
        concluida.setIdTarefa(1L);
        concluida.setConcluida(true);

        when(tarefaRepository.findByUsuarioIdUsuario(1L)).thenReturn(List.of(concluida));

        List<TarefaResponseDTO> tarefas = service.getTarefasByUsuario(1L);

        assertEquals(1, tarefas.size());
        assertEquals(true, tarefas.get(0).concluida());
    }

    @Test
    void completarTarefa_deveLancarQuandoTarefaNaoEncontrada() {
        when(tarefaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.completarTarefa(1L));
    }

    @Test
    void completarTarefa_deveMarcarComoConcluida() {
        Tarefa tarefa = new Tarefa();
        tarefa.setIdTarefa(1L);
        tarefa.setConcluida(false);

        when(tarefaRepository.findById(1L)).thenReturn(java.util.Optional.of(tarefa));

        service.completarTarefa(1L);

        assertEquals(true, tarefa.getConcluida());
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void reabrirTarefa_deveMarcarComoNaoConcluida() {
        Tarefa tarefa = new Tarefa();
        tarefa.setIdTarefa(1L);
        tarefa.setConcluida(true);

        when(tarefaRepository.findById(1L)).thenReturn(java.util.Optional.of(tarefa));

        service.reabrirTarefa(1L);

        assertEquals(false, tarefa.getConcluida());
        verify(tarefaRepository).save(tarefa);
    }

    @Test
    void deleteTarefa_deveLancarQuandoTarefaNaoEncontrada() {
        when(tarefaRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteTarefa(1L));
    }

    @Test
    void getTarefasByMateria_deveRetornarListaDoRepositorio() {
        when(tarefaRepository.findByMateriaIdMateria(1L)).thenReturn(Collections.emptyList());

        List<TarefaResponseDTO> tarefas = service.getTarefasByMateria(1L);

        assertNotNull(tarefas);
        assertTrue(tarefas.isEmpty());
    }
}
