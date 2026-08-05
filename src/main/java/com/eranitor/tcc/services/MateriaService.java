package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.MateriaDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MateriaService {
    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;



    public void cadastrarMateria(MateriaDTO dto, Long usuarioId) {
        if (materiaRepository.findByNomeIgnoreCaseAndUsuarioId(dto.nome(), usuarioId).isPresent()) {
            throw new RuntimeException("Você já possui uma matéria com esse nome.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Materia materia = new Materia();

        materia.setNome(dto.nome());
        materia.setUsuario(usuario);
        materia.setAtiva(Boolean.TRUE);

        materiaRepository.save(materia);
    }

    public void updateMateria(MateriaDTO dto, Long usuarioId, Integer id) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Matéria não encontrada!"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

       if (dto.nome() != null && !dto.nome().isBlank()) {
           materia.setNome(dto.nome());
       }

       if (dto.ativa() != null) {
           materia.setAtiva(dto.ativa());
       }

       materiaRepository.save(materia);
    }

}
