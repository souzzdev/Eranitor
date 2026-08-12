package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.CreateTopicoDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Topico;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    public void cadastrarTopico(CreateTopicoDTO dto, Long materiaId){
        if (topicoRepository.findByNomeIgnoreCaseAndMateria_IdMateria(dto.nome(), materiaId).isPresent()) {
            throw new IllegalArgumentException("Tópico já existe.");
        }

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada."));

        Topico topico = new Topico();

        topico.setNome(dto.nome());
        topico.setMateria(materia);
        topico.setConcluido(false);

        topicoRepository.save(topico);
    }
}
