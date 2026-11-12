package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesempenhoServiceTest {

    @Mock
    private DesempenhoRepository desempenhoRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private TempoEstudoService tempoEstudoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private DesempenhoService service;

    @Test
    void calcularDesempenho_quandoNaoHaRegistrosDeveRetornarHorasZero() {
        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of());

        DesempenhoDTO dto = service.calcularDesempenho(1L, 1L, 10, 2026);

        assertNotNull(dto);
        assertEquals(0, dto.horasConcluidas().intValue());
        assertEquals(0, dto.horasMeta().intValue());
        assertEquals(10, dto.mes().intValue());
        assertEquals(2026, dto.ano().intValue());
    }

    @Test
    void calcularDesempenho_deveSomarHorasConcluidasEHorasMeta() {
        Desempenho desempenho = new Desempenho();
        desempenho.setHorasConcluidas(2);
        desempenho.setHorasMeta(4);

        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of(desempenho));

        DesempenhoDTO dto = service.calcularDesempenho(1L, 1L, 10, 2026);

        assertEquals(2, dto.horasConcluidas().intValue());
        assertEquals(4, dto.horasMeta().intValue());
    }

    @Test
    void calcularDesempenho_deveValidarDadosDeEntrada() {
        assertThrows(IllegalArgumentException.class, () -> service.calcularDesempenho(0L, 1L, 10, 2026));
        assertThrows(IllegalArgumentException.class, () -> service.calcularDesempenho(null, 1L, 10, 2026));
        assertThrows(IllegalArgumentException.class, () -> service.calcularDesempenho(1L, null, 10, 2026));
        assertThrows(IllegalArgumentException.class, () -> service.calcularDesempenho(1L, 1L, 0, 2026));
        assertThrows(IllegalArgumentException.class, () -> service.calcularDesempenho(1L, 1L, 13, 2026));
        assertThrows(IllegalArgumentException.class, () -> service.calcularDesempenho(1L, 1L, 10, 0));
    }

    @Test
    void calcularProgresso_deveRetornarZeroQuandoHorasMetaInvalida() {
        assertEquals(0.0, service.calcularProgresso(50, 0), 0.0);
        assertEquals(0.0, service.calcularProgresso(50, null), 0.0);
    }

    @Test
    void calcularProgresso_deveRetornarPercentualCorreto() {
        assertEquals(50.0, service.calcularProgresso(50, 100), 0.0);
        assertEquals(25.0, service.calcularProgresso(25, 100), 0.0);
    }

    @Test
    void registrarDesempenho_deveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.registrarDesempenho(1L, 1L, 10, 2026, 3));
    }

    @Test
    void registrarDesempenho_deveLancarQuandoMateriaNaoExiste() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.registrarDesempenho(1L, 1L, 10, 2026, 3));
    }

    @Test
    void registrarDesempenho_deveRetornarDesempenhoCriado() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setIdMateria(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of());

        Desempenho salvo = new Desempenho();
        salvo.setIdDesempenho(5L);
        salvo.setUsuario(usuario);
        salvo.setMateria(materia);
        salvo.setMes(10);
        salvo.setAno(2026);
        salvo.setHorasConcluidas(3);
        salvo.setHorasMeta(0);

        when(desempenhoRepository.save(any(Desempenho.class))).thenReturn(salvo);

        DesempenhoDTO dto = service.registrarDesempenho(1L, 1L, 10, 2026, 3);

        assertNotNull(dto);
        assertEquals(5L, dto.id());
        assertEquals(1L, dto.usuarioId());
        assertEquals(1L, dto.materiaId());
        assertEquals(10, dto.mes().intValue());
        assertEquals(2026, dto.ano().intValue());
        assertEquals(3, dto.horasConcluidas().intValue());
    }

    @Test
    void getDesempenhoUsuario_deveRetornarListaVaziaQuandoNaoHaRegistros() {
        when(desempenhoRepository.findByUsuario_IdUsuarioAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of());

        List<DesempenhoDTO> lista = service.getDesempenhoUsuario(1L, 10, 2026);

        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    void setMetaSemanal_deveLancarQuandoHorasMetaInvalida() {
        assertThrows(IllegalArgumentException.class, () ->
                service.setMetaSemanal(1L, 1L, 10, 2026, 0));
        assertThrows(IllegalArgumentException.class, () ->
                service.setMetaSemanal(1L, 1L, 10, 2026, null));
    }

    @Test
    void setMetaSemanal_deveAtualizarMetaSemanal() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setIdMateria(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        Desempenho desempenho = new Desempenho();
        desempenho.setUsuario(usuario);
        desempenho.setMateria(materia);
        desempenho.setMes(10);
        desempenho.setAno(2026);
        desempenho.setHorasConcluidas(2);
        desempenho.setHorasMeta(4);

        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(1L, 10, 2026))
                .thenReturn(List.of(desempenho));

        service.setMetaSemanal(1L, 1L, 10, 2026, 6);

        assertEquals(6, desempenho.getHorasMeta().intValue());
    }
}
