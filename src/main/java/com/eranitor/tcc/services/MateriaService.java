package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.MateriaDTO;
import com.eranitor.tcc.dto.MateriaResponseDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.infra.security.OwnershipService;
import com.eranitor.tcc.mapper.MateriaMapper;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MateriaService {
    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaMapper materiaMapper;

    @Autowired
    private OwnershipService ownership;


    public void cadastrarMateria(MateriaDTO dto, Long usuarioId) {
        ownership.exigirUsuario(usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (materiaRepository.findByNomeIgnoreCaseAndUsuario_IdUsuario(dto.nome(), usuarioId).isPresent()) {
            throw new RuntimeException("Você já possui uma matéria com esse nome.");
        }

        Materia materia = materiaMapper.toEntity(dto);

        materia.setUsuario(usuario);
        materia.setAtiva(Boolean.TRUE);


        materiaRepository.save(materia);
    }

    public MateriaResponseDTO updateMateria(MateriaDTO dto, Long usuarioId, Long id) {
        ownership.exigirUsuario(usuarioId);

        Materia materia = materiaRepository.findByIdMateriaAndUsuario_IdUsuario(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Matéria não encontrada ou não pertence ao usuário!"
                ));

        if (materiaRepository.existsByNomeIgnoreCaseAndUsuario_IdUsuarioAndIdMateriaNot(
                dto.nome(),
                usuarioId,
                id
        )) {
            throw new IllegalArgumentException("Você já possui uma matéria com esse nome.");
        }

        materiaMapper.updateEntity(dto, materia);

        Materia materiaAtualizada = materiaRepository.save(materia);

        return materiaMapper.toResponseDTO(materiaAtualizada);
    }


    public void deleteMateria(Long id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada!"));

        ownership.exigirDono(materia.getUsuario());

        materiaRepository.deleteById(id);
    }

    public List<MateriaResponseDTO> getTodasMaterias(Long usuarioId) {
        ownership.exigirUsuario(usuarioId);

        return materiaRepository.findAllByUsuario_IdUsuario(usuarioId)
                .stream()
                .map(materiaMapper::toResponseDTO)
                .toList();

    }

    public List<MateriaResponseDTO> getMateriasAtivas(Long usuarioId) {
        ownership.exigirUsuario(usuarioId);

        return materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(usuarioId)
                .stream()
                .map(materiaMapper::toResponseDTO)
                .toList();
    }



}
