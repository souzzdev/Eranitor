package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.TempoEstudoDTO;
import com.eranitor.tcc.dto.TempoEstudoRegistroDTO;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.TempoEstudo;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TempoEstudoRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TempoEstudoService {

    @Autowired
    private TempoEstudoRepository tempoEstudoRepository;

    @Autowired
    MateriaRepository materiaRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    public TempoEstudoDTO registrarTempo(TempoEstudoRegistroDTO dto, Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        if (dto == null || dto.minutos() == null || dto.minutos() <= 0) {
            throw new IllegalArgumentException("Os minutos de estudo devem ser maiores que zero.");
        }

        if (dto.materiaId() == null) {
            throw new IllegalArgumentException("A matéria é obrigatória.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Materia materia = materiaRepository.findById(dto.materiaId())
                .orElseThrow(() -> new RuntimeException("Matéria não encontrada"));

        TempoEstudo tempoEstudo = new TempoEstudo();
        tempoEstudo.setUsuario(usuario);
        tempoEstudo.setMateria(materia);
        tempoEstudo.setMinutosAtividade(0);
        tempoEstudo.setMinutosVideo(0);
        tempoEstudo.setMinutosApostila(0);

        switch (dto.tipo()) {
            case "EXERCICIO" -> tempoEstudo.setMinutosAtividade(dto.minutos());
            case "VIDEO" -> tempoEstudo.setMinutosVideo(dto.minutos());
            case "APOSTILA" -> tempoEstudo.setMinutosApostila(dto.minutos());

            default -> throw new IllegalArgumentException("Tipo de estudo inválido");
        }

        tempoEstudo.setMes(java.time.LocalDate.now().getMonthValue());
        tempoEstudo.setAno(java.time.LocalDate.now().getYear());

        TempoEstudo salvo = tempoEstudoRepository.save(tempoEstudo);

        return new TempoEstudoDTO(
                salvo.getIdTempoEstudo(),
                salvo.getUsuario().getIdUsuario(),
                salvo.getMateria().getIdMateria(),
                salvo.getMes(),
                salvo.getAno(),
                salvo.getMinutosAtividade(),
                salvo.getMinutosVideo(),
                salvo.getMinutosApostila()
        );
    }

    public List<TempoEstudoDTO> listarPorUsuario (Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        List<TempoEstudo> registros = tempoEstudoRepository.findByUsuario_IdUsuario(usuarioId);

        return registros.stream()
                .map(t -> new TempoEstudoDTO(
                        t.getIdTempoEstudo(),
                        t.getUsuario().getIdUsuario(),
                        t.getMateria().getIdMateria(),
                        t.getMes(),
                        t.getAno(),
                        t.getMinutosAtividade(),
                        t.getMinutosVideo(),
                        t.getMinutosApostila()
                ))
                .toList();
    }

    public List<TempoEstudoDTO> listarPorMateria (Long materiaId) {
        if (materiaId == null) {
            throw new IllegalArgumentException("A matéria não pode ser nula.");
        }

        List<TempoEstudo> registros = tempoEstudoRepository.findByMateria_IdMateria(materiaId);

        return registros.stream()
                .map(t -> new TempoEstudoDTO(
                        t.getIdTempoEstudo(),
                        t.getUsuario().getIdUsuario(),
                        t.getMateria().getIdMateria(),
                        t.getMes(),
                        t.getAno(),
                        t.getMinutosAtividade(),
                        t.getMinutosVideo(),
                        t.getMinutosApostila()
                ))
                .toList();
    }
}
