package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.DesempenhoDTO;
import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.MateriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DesempenhoService {
    private final DesempenhoRepository desempenhoRepository;
    private final MateriaRepository materiaRepository;
    private final TempoEstudoService tempoEstudoService;

    public DesempenhoService(DesempenhoRepository desempenhoRepository, MateriaRepository materiaRepository, TempoEstudoService tempoEstudoService) {
        this.desempenhoRepository = desempenhoRepository;
        this.materiaRepository = materiaRepository;
        this.tempoEstudoService = tempoEstudoService;
    }

    public DesempenhoDTO calcularDesempenho(Long usuarioId, Long materiaId, Integer mes, Integer ano) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("O ID do usuário deve ser positivo");
        }

        if (materiaId == null) {
            throw new IllegalArgumentException("A matéria é obrigatória");
        }

        if (mes == null || mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve estar entre 1 e 12");
        }

        if (ano == null || ano < 1) {
            throw new IllegalArgumentException("O ano deve ser válido");
        }

        List<Desempenho> registros = desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(materiaId, mes, ano);

        Integer horasConcluidas = 0;
        Integer horasMeta = 0;

        for (Desempenho d : registros) {
            if (d.getHorasConcluidas() != null) horasConcluidas += d.getHorasConcluidas();
            if (d.getHorasMeta() != null) horasMeta += d.getHorasMeta();
        }

        return new DesempenhoDTO(
                null,
                usuarioId,
                materiaId,
                mes,
                ano,
                horasConcluidas,
                horasMeta
        );
    }

    public double calcularProgresso(Integer horasConcluidas, Integer horasMeta) {
        if (horasMeta == null || horasMeta <= 0) {
            return 0.0;
        }

        return (horasConcluidas == null ? 0.0 : horasConcluidas) / horasMeta * 100.0;
    }
}
