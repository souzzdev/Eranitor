package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AgendaCreateDTO;
import com.eranitor.tcc.dto.AgendaDTO;
import com.eranitor.tcc.entity.Agenda;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.AgendaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AgendaService service;

    @Test
    void criarAgenda_deveLancarQuandoUsuarioIdInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                service.criarAgenda(new AgendaCreateDTO(LocalDate.now(), "Revisão", "ESTUDO"), 0L));
    }

    @Test
    void criarAgenda_deveLancarQuandoTituloVazio() {
        assertThrows(IllegalArgumentException.class, () ->
                service.criarAgenda(new AgendaCreateDTO(LocalDate.now(), "  ", "ESTUDO"), 1L));
    }

    @Test
    void criarAgenda_deveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.criarAgenda(new AgendaCreateDTO(LocalDate.now(), "Revisão", "ESTUDO"), 1L));
    }

    @Test
    void criarAgenda_deveRetornarAgendaCriada() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Agenda agenda = new Agenda();
        agenda.setId(1);
        agenda.setUsuario(usuario);
        agenda.setData(LocalDate.now());
        agenda.setTitulo("Revisão");
        agenda.setTipo("ESTUDO");

        when(agendaRepository.save(any(Agenda.class))).thenReturn(agenda);

        AgendaDTO dto = service.criarAgenda(new AgendaCreateDTO(LocalDate.now(), "Revisão", "ESTUDO"), 1L);

        assertNotNull(dto);
        assertEquals(1, dto.id().intValue());
        assertEquals(1L, dto.usuarioId());
        assertEquals("Revisão", dto.titulo());
        assertEquals("ESTUDO", dto.tipo());
    }

    @Test
    void listarPorUsuario_deveRetornarListaDeAgendas() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Agenda agenda = new Agenda();
        agenda.setId(1);
        agenda.setUsuario(usuario);
        agenda.setData(LocalDate.now());
        agenda.setTitulo("Revisão");
        agenda.setTipo("ESTUDO");

        when(agendaRepository.findByUsuario_IdUsuario(1L)).thenReturn(List.of(agenda));

        List<AgendaDTO> dtos = service.listarPorUsuario(1L);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals("Revisão", dtos.get(0).titulo());
    }

    @Test
    void atualizarAgenda_deveLancarQuandoIdNulo() {
        assertThrows(IllegalArgumentException.class, () ->
                service.atualizarAgenda(null, new AgendaCreateDTO(LocalDate.now(), "Revisão", "ESTUDO")));
    }

    @Test
    void deletarAgenda_deveLancarQuandoIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.deletarAgenda(null));
    }
}
