package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.TarefaDTO;
import com.eranitor.tcc.dto.TopicoDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TarefaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    public void createTarefa(Long usuarioId, Long materiaId, TarefaDTO dto) {
        if(!tarefaRepository.existsByTituloIgnoreCaseAndMateria_IdMateriaAndUsuario_IdUsuario(
                dto.titulo(),
                materiaId,
                usuarioId
        )) {
            throw new RuntimeException("Você já possui uma tarefa com esse título nessa matéria.");
        }


        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));


        Tarefa tarefa = new Tarefa();

        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setDataVencimento(dto.dataVencimento());
        tarefa.setCor(dto.cor());
        tarefa.setCriadoEm(LocalDate.now());
        tarefa.setConcluida(false);
        tarefa.setUsuario(usuario);
        tarefa.setMateria(materia);

        tarefaRepository.save(tarefa);

    }


}

