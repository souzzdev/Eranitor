package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.CreateTopicoDTO;
import com.eranitor.tcc.dto.TopicoDTO;
import com.eranitor.tcc.dto.TopicoResponseDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Topico;
import com.eranitor.tcc.infra.security.OwnershipService;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private OwnershipService ownership;

    public void cadastrarTopico(CreateTopicoDTO dto, Long materiaId){
        if (topicoRepository.findByNomeIgnoreCaseAndMateria_IdMateria(dto.nome(), materiaId).isPresent()) {
            throw new IllegalArgumentException("Tópico já existe.");
        }

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada."));

        ownership.exigirDono(materia.getUsuario());

        Topico topico = new Topico();

        topico.setNome(dto.nome());
        topico.setMateria(materia);
        topico.setConcluido(false);

        topicoRepository.save(topico);
    }

    public void updateTopico(Long id, TopicoDTO dto, Long materiaId) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado"));

        ownership.exigirDono(topico.getMateria() == null
                ? null
                : topico.getMateria().getUsuario());

        Materia materia = materiaRepository.findById(materiaId)
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada."));

        ownership.exigirDono(materia.getUsuario());

        if (dto.nome() != null && !dto.nome().isBlank()) {
            topico.setNome(dto.nome());
        }

        if (dto.concluido() != null) {
            topico.setConcluido(dto.concluido());
        }

        topicoRepository.save(topico);
    }

    public void deleteTopico(Long id) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado."));

        ownership.exigirDono(topico.getMateria() == null
                ? null
                : topico.getMateria().getUsuario());

        topicoRepository.deleteById(id);
    }

    public List<TopicoResponseDTO> getTopicosByMateria(Long materiaId) {
        materiaRepository.findById(materiaId)
                .ifPresent(materia -> ownership.exigirDono(materia.getUsuario()));

        return topicoRepository.findAllByMateria_IdMateria(materiaId)
                .stream()
                .map(TopicoResponseDTO::from)
                .toList();
    }

    public void marcarConcluido(Long id) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico não encontrado."));

        ownership.exigirDono(topico.getMateria() == null
                ? null
                : topico.getMateria().getUsuario());

        topico.setConcluido(true);

        topicoRepository.save(topico);
    }
}
