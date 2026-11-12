package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.MateriaDTO;
import com.eranitor.tcc.dto.MateriaResponseDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.mapper.MateriaMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MateriaMapper materiaMapper;

    @InjectMocks
    private MateriaService service;

    @Test
    void cadastrarMateria_deveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.cadastrarMateria(new MateriaDTO("Matemática", true, 2), 1L));
    }

    @Test
    void cadastrarMateria_deveLancarQuandoMateriaDuplicada() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findByNomeIgnoreCaseAndUsuario_IdUsuario("Matemática", 1L))
                .thenReturn(Optional.of(new Materia()));

        assertThrows(RuntimeException.class, () ->
                service.cadastrarMateria(new MateriaDTO("Matemática", true, 2), 1L));
    }

    @Test
    void cadastrarMateria_deveSalvarMateria() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        Materia materia = new Materia();
        materia.setNome("Matemática");
        materia.setAtiva(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.findByNomeIgnoreCaseAndUsuario_IdUsuario("Matemática", 1L))
                .thenReturn(Optional.empty());
        when(materiaMapper.toEntity(any(MateriaDTO.class))).thenReturn(materia);

        service.cadastrarMateria(new MateriaDTO("Matemática", true, 2), 1L);

        verify(materiaRepository).save(any(Materia.class));
    }

    @Test
    void updateMateria_deveLancarQuandoMatériaNaoEncontrada() {
        when(materiaRepository.findByIdMateriaAndUsuario_IdUsuario(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.updateMateria(new MateriaDTO("Matemática", true, 2), 1L, 1L));
    }

    @Test
    void updateMateria_deveLancarQuandoNomeDuplicadoEmOutraMateria() {
        Materia materia = new Materia();
        materia.setIdMateria(1L);

        when(materiaRepository.findByIdMateriaAndUsuario_IdUsuario(1L, 1L))
                .thenReturn(Optional.of(materia));
        when(materiaRepository.existsByNomeIgnoreCaseAndUsuario_IdUsuarioAndIdMateriaNot("Física", 1L, 1L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.updateMateria(new MateriaDTO("Física", true, 2), 1L, 1L));
    }

    @Test
    void updateMateria_deveAtualizarEMostrarMateria() {
        Materia materia = new Materia();
        materia.setIdMateria(1L);
        materia.setNome("Matemática");

        MateriaResponseDTO responseDTO = new MateriaResponseDTO(1L, "Matemática", true, 2);

        when(materiaRepository.findByIdMateriaAndUsuario_IdUsuario(1L, 1L))
                .thenReturn(Optional.of(materia));
        when(materiaRepository.existsByNomeIgnoreCaseAndUsuario_IdUsuarioAndIdMateriaNot("Física", 1L, 1L))
                .thenReturn(false);
        when(materiaMapper.updateEntity(any(MateriaDTO.class), any(Materia.class))).thenReturn(materia);
        when(materiaRepository.save(any(Materia.class))).thenReturn(materia);
        when(materiaMapper.toResponseDTO(any(Materia.class))).thenReturn(responseDTO);

        MateriaResponseDTO atualizado = service.updateMateria(new MateriaDTO("Física", true, 2), 1L, 1L);

        assertNotNull(atualizado);
        assertEquals(1L, atualizado.idMateria());
        assertEquals("Matemática", atualizado.nome());
    }

    @Test
    void deleteMateria_deveLancarQuandoMatériaNaoEncontrada() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteMateria(1L));
    }

    @Test
    void getTodasMaterias_deveRetornarLista() {
        when(materiaRepository.findAllByUsuario_IdUsuario(1L)).thenReturn(List.of());

        assertNotNull(service.getTodasMaterias(1L));
    }

    @Test
    void getMateriasAtivas_deveRetornarLista() {
        when(materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(1L)).thenReturn(List.of());

        assertNotNull(service.getMateriasAtivas(1L));
    }
}
