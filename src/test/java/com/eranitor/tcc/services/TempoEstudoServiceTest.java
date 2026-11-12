package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.TempoEstudoDTO;
import com.eranitor.tcc.dto.TempoEstudoRegistroDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.TempoEstudo;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TempoEstudoRepository;
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
class TempoEstudoServiceTest {

    @Mock
    private TempoEstudoRepository tempoEstudoRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TempoEstudoService service;

    @Test
    void registrarTempo_deveLancarQuandoUsuarioIdInvalido() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarTempo(new TempoEstudoRegistroDTO("VIDEO", 30, 1L), 0L));
    }

    @Test
    void registrarTempo_deveLancarQuandoMinutosInvalidos() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarTempo(new TempoEstudoRegistroDTO("VIDEO", 0, 1L), 1L));
    }

    @Test
    void registrarTempo_deveLancarQuandoTipoInvalido() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setIdMateria(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        assertThrows(IllegalArgumentException.class, () ->
                service.registrarTempo(new TempoEstudoRegistroDTO("INVALIDO", 30, 1L), 1L));
    }

    @Test
    void registrarTempo_deveRetornarDTOComMinutosDoTipoInformado() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setIdMateria(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        TempoEstudo salvo = new TempoEstudo();
        salvo.setIdTempoEstudo(3);
        salvo.setUsuario(usuario);
        salvo.setMateria(materia);
        salvo.setMinutosAtividade(0);
        salvo.setMinutosVideo(60);
        salvo.setMinutosApostila(0);
        salvo.setMes(LocalDate.now().getMonthValue());
        salvo.setAno(LocalDate.now().getYear());

        when(tempoEstudoRepository.save(any(TempoEstudo.class))).thenReturn(salvo);

        TempoEstudoDTO dto = service.registrarTempo(new TempoEstudoRegistroDTO("VIDEO", 60, 1L), 1L);

        assertNotNull(dto);
        assertEquals(3, dto.id().intValue());
        assertEquals(1L, dto.usuarioId());
        assertEquals(1L, dto.materiaId());
        assertEquals(60, dto.minutosVideo().intValue());
        assertEquals(LocalDate.now().getMonthValue(), dto.mes().intValue());
        assertEquals(LocalDate.now().getYear(), dto.ano().intValue());
    }

    @Test
    void listarPorUsuario_deveRetornarListaDeDTOs() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setIdMateria(1L);

        TempoEstudo registro = new TempoEstudo();
        registro.setIdTempoEstudo(1);
        registro.setUsuario(usuario);
        registro.setMateria(materia);
        registro.setMinutosAtividade(30);

        when(tempoEstudoRepository.findByUsuario_IdUsuario(1L)).thenReturn(List.of(registro));

        List<TempoEstudoDTO> dtos = service.listarPorUsuario(1L);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(30, dtos.get(0).minutosAtividade().intValue());
    }

    @Test
    void listarPorTarefa_deveLancarQuandoMateriaIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.listarPorTarefa(null));
    }
}
