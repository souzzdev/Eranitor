package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.CreateTopicoDTO;
import com.eranitor.tcc.dto.TopicoDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Topico;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TopicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopicoServiceTest {

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private TopicoService service;

    @Test
    void cadastrarTopico_deveLancarQuandoTopicoDuplicado() {
        when(topicoRepository.findByNomeIgnoreCaseAndMateria_IdMateria("Top", 1L))
                .thenReturn(Optional.of(new Topico()));

        assertThrows(IllegalArgumentException.class, () ->
                service.cadastrarTopico(new CreateTopicoDTO("Top"), 1L));
    }

    @Test
    void cadastrarTopico_deveLancarQuandoMateriaNaoEncontrada() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.cadastrarTopico(new CreateTopicoDTO("Top"), 1L));
    }

    @Test
    void cadastrarTopico_deveSalvarTopico() {
        Materia materia = new Materia();
        materia.setIdMateria(1L);

        Topico topico = new Topico();
        topico.setNome("Top");
        topico.setMateria(materia);
        topico.setConcluido(false);

        when(topicoRepository.findByNomeIgnoreCaseAndMateria_IdMateria("Top", 1L))
                .thenReturn(Optional.empty());
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        service.cadastrarTopico(new CreateTopicoDTO("Top"), 1L);

        verify(topicoRepository).save(any(Topico.class));
    }

    @Test
    void updateTopico_deveLancarQuandoTopicoNaoEncontrado() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.updateTopico(1L, new TopicoDTO("Novo", false), 1L));
    }

    @Test
    void updateTopico_deveAtualizarTopico() {
        Topico topico = new Topico();
        topico.setIdTopico(1L);
        topico.setNome("Top");
        topico.setConcluido(false);

        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(new Materia()));

        service.updateTopico(1L, new TopicoDTO("Novo", true), 1L);

        assertEquals("Novo", topico.getNome());
        assertEquals(true, topico.getConcluido());
        verify(topicoRepository).save(topico);
    }

    @Test
    void deleteTopico_deveLancarQuandoTopicoNaoEncontrado() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteTopico(1L));
    }

    @Test
    void marcarConcluido_deveLancarQuandoTopicoNaoEncontrado() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.marcarConcluido(1L));
    }

    @Test
    void marcarConcluido_deveMarcarComoConcluido() {
        Topico topico = new Topico();
        topico.setIdTopico(1L);
        topico.setConcluido(false);

        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));

        service.marcarConcluido(1L);

        assertEquals(true, topico.getConcluido());
        verify(topicoRepository).save(topico);
    }

    @Test
    void getTopicosByMateria_deveRetornarLista() {
        when(topicoRepository.findAllByMateria_IdMateria(1L)).thenReturn(List.of());

        assertNotNull(service.getTopicosByMateria(1L));
    }
}
