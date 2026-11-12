package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AgendaDTO;
import com.eranitor.tcc.dto.RelatorioDTO;
import com.eranitor.tcc.dto.TempoEstudoDTO;
import com.eranitor.tcc.entity.Agenda;
import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.TempoEstudo;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.AgendaRepository;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.TempoEstudoRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DesempenhoRepository desempenhoRepository;

    @Mock
    private TempoEstudoRepository tempoEstudoRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private RelatorioService service;

    @Test
    void gerarRelatorioPeriodo_deveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                service.gerarRelatorioPeriodo(1L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31)));
    }

    @Test
    void gerarRelatorioPeriodo_deveRetornarRelatorioDoPeriodo() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setIdMateria(2L);
        materia.setNome("Matemática");

        Desempenho desempenho = new Desempenho();
        desempenho.setIdDesempenho(1L);
        desempenho.setUsuario(usuario);
        desempenho.setMateria(materia);
        desempenho.setHorasConcluidas(2);
        desempenho.setHorasMeta(4);

        TempoEstudo tempoEstudo = new TempoEstudo();
        tempoEstudo.setIdTempoEstudo(1);
        tempoEstudo.setUsuario(usuario);
        tempoEstudo.setMateria(materia);
        tempoEstudo.setMinutosAtividade(60);

        Agenda agenda = new Agenda();
        agenda.setId(1);
        agenda.setUsuario(usuario);
        agenda.setTitulo("Revisão");

        when(desempenhoRepository.findByUsuario_IdUsuarioAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of(desempenho));
        when(tempoEstudoRepository.findByUsuario_IdUsuarioAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of(tempoEstudo));
        when(agendaRepository.findByUsuario_IdUsuarioAndDataBetween(1L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31)))
                .thenReturn(List.of(agenda));

        RelatorioDTO relatorio = service.gerarRelatorioPeriodo(1L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));

        assertNotNull(relatorio);
        assertEquals(1, relatorio.desempenho().size());
        assertEquals(1, relatorio.tempoEstudo().size());
        assertEquals(1, relatorio.eventos().size());
        assertEquals("2026-10-01 a 2026-10-31", relatorio.periodo());
    }

    @Test
    void gerarRelatorioMateria_deveRetornarRelatorioDaMateria() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setIdMateria(2L);

        Desempenho desempenho = new Desempenho();
        desempenho.setIdDesempenho(1L);
        desempenho.setUsuario(usuario);
        desempenho.setMateria(materia);
        desempenho.setHorasConcluidas(2);
        desempenho.setHorasMeta(4);

        TempoEstudo tempoEstudo = new TempoEstudo();
        tempoEstudo.setIdTempoEstudo(1);
        tempoEstudo.setUsuario(usuario);
        tempoEstudo.setMateria(materia);
        tempoEstudo.setMinutosAtividade(60);

        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(2L, 10, 2026))
                .thenReturn(List.of(desempenho));
        when(tempoEstudoRepository.findByMateria_IdMateriaAndMesAndAno(2L, 10, 2026))
                .thenReturn(List.of(tempoEstudo));

        RelatorioDTO relatorio = service.gerarRelatorioMateria(2L, LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31));

        assertNotNull(relatorio);
        assertEquals(1, relatorio.desempenho().size());
        assertEquals(1, relatorio.tempoEstudo().size());
        assertEquals(0, relatorio.eventos().size());
        assertEquals("2026-10-01 a 2026-10-31", relatorio.periodo());
    }

    @Test
    void gerarRelatorioTempoEstudo_deveRetornarRelatorioDeTempoDeEstudo() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(tempoEstudoRepository.findByUsuario_IdUsuarioAndMesAndAno(1L, 10, 2026))
                .thenReturn(Collections.emptyList());

        RelatorioDTO relatorio = service.gerarRelatorioTempoEstudo(1L, 10, 2026);

        assertNotNull(relatorio);
        assertEquals("10/2026", relatorio.periodo());
        assertEquals(0, relatorio.tempoEstudo().size());
    }
}
