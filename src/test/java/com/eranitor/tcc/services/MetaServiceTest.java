package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.DistribuicaoMateriaDTO;
import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private DesempenhoRepository desempenhoRepository;

    @Mock
    private ProgressoService progressoService;

    @InjectMocks
    private MetaService service;

    @Test
    void setMetaSemanal_deveLancarQuandoMetaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> service.setMetaSemanal(1L, 0));
        assertThrows(IllegalArgumentException.class, () -> service.setMetaSemanal(1L, 169));
        assertThrows(NullPointerException.class, () -> service.setMetaSemanal(1L, null));
    }

    @Test
    void setMetaSemanal_deveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.setMetaSemanal(1L, 10));
    }

    @Test
    void setMetaSemanal_deveSalvarMetaDoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(usuario));

        service.setMetaSemanal(1L, 10);

        assertEquals(10, usuario.getMetaSemanalHoras().intValue());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void setMetaMateria_deveLancarQuandoHorasMetaInvalida() {
        when(materiaRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.setMetaMateria(1L, 1L, 10, 2026, 0));
    }

    @Test
    void setMetaMateria_deveLancarQuandoMateriaNaoEncontrada() {
        when(materiaRepository.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                service.setMetaMateria(1L, 99L, 10, 2026, 5));
    }

    @Test
    void setMetaMateria_deveSalvarMetaDaMateria() {
        Desempenho desempenho = new Desempenho();
        desempenho.setHorasMeta(4);

        when(materiaRepository.existsById(1L)).thenReturn(true);
        when(desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(1L, 10, 2026))
                .thenReturn(java.util.List.of(desempenho));

        service.setMetaMateria(1L, 1L, 10, 2026, 6);

        assertEquals(6, desempenho.getHorasMeta().intValue());
        verify(desempenhoRepository).save(desempenho);
    }

    @Test
    void getMetaSemanal_deveRetornarMetaDoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setMetaSemanalHoras(8);

        when(usuarioRepository.findById(1L)).thenReturn(java.util.Optional.of(usuario));

        Integer meta = service.getMetaSemanal(1L);

        assertNotNull(meta);
        assertEquals(8, meta.intValue());
    }

    @Test
    void getDistribuicaoPorMateria_deveRetornarLista() {
        Materia materia = new Materia();
        materia.setIdMateria(1L);
        materia.setNome("Matemática");
        materia.setDificuldade(2);

        when(materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(1L))
                .thenReturn(java.util.List.of(materia));

        List<DistribuicaoMateriaDTO> distribuicao = service.getDistribuicaoPorMateria(1L, 10, 2026);

        assertNotNull(distribuicao);
        assertEquals(1, distribuicao.size());
    }

    @Test
    void calcularDesempenhoPercentual_deveRetornarProgressoGeral() {
        when(progressoService.calcularProgressoGeral(1L)).thenReturn(50.0);

        assertEquals(50.0, service.calcularDesempenhoPercentual(1L), 0.0);
    }

    @Test
    void sugerirDistribuicao_deveLancarQuandoUsuarioNaoTemMateriaAtiva() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(1L))
                .thenReturn(Collections.emptyList());

        assertThrows(IllegalStateException.class, () -> service.sugerirDistribuicao(1L, 4));
    }

    @Test
    void sugerirDistribuicao_deveRetornarDistribuicaoSugerida() {
        Materia m1 = new Materia();
        m1.setIdMateria(1L);
        m1.setNome("Matemática");
        m1.setDificuldade(1);

        Materia m2 = new Materia();
        m2.setIdMateria(2L);
        m2.setNome("Física");
        m2.setDificuldade(3);

        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(1L))
                .thenReturn(java.util.List.of(m1, m2));

        List<DistribuicaoMateriaDTO> distribuicao = service.sugerirDistribuicao(1L, 4);

        assertNotNull(distribuicao);
        assertEquals(2, distribuicao.size());
        assertEquals(3, distribuicao.get(0).horasSugeridas().intValue());
        assertEquals(1, distribuicao.get(1).horasSugeridas().intValue());
    }
}
