package com.eranitor.tcc.services;

import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProgressoService {

    private final TarefaRepository tarefaRepository;
    private final MateriaRepository materiaRepository;
    private final DesempenhoRepository desempenhoRepository;

    public ProgressoService (TarefaRepository tarefaRepository, MateriaRepository materiaRepository, DesempenhoRepository desempenhoRepository) {
        this.tarefaRepository = tarefaRepository;
        this.materiaRepository = materiaRepository;
        this.desempenhoRepository = desempenhoRepository;
    }

    private double percentual(long valor, long total) {
        if (total <= 0) {
            return 0.0;
        }

        return (double) valor / total * 100.0;
    }


    public double calcularProgressoGeral(Long usuarioId) {
        Objects.requireNonNull(usuarioId, "O ID do usuário não pode ser nulo.");

        long total = tarefaRepository.countByUsuarioIdUsuario(usuarioId);

        if (total == 0) {
            return 0.0;
        }

        long concluidas = tarefaRepository.countByUsuarioIdUsuarioAndConcluida(usuarioId, true);

        return percentual(concluidas, total);
    }

    public double calcularProgressoMateria(Long materiaId) {
        Objects.requireNonNull(materiaId, "O ID da matéria não pode ser nulo.");

        long total = tarefaRepository.countByMateriaIdMateria(materiaId);

        if (total == 0) {
            return 0.0;
        }

        long concluidas = tarefaRepository.countByMateriaIdMateriaAndConcluida(materiaId, true);

        return percentual(concluidas, total);
    }

    public Map<Long, Double> getProgressoPorMateria(Long usuarioId) {
        Objects.requireNonNull(usuarioId, "O ID do usuário não pode ser nulo.");

        List<Materia> materias = materiaRepository.findAllByUsuario_IdUsuario(usuarioId);

        List<Tarefa> tarefas = tarefaRepository.findByUsuarioIdUsuario(usuarioId);

        Map<Long, long[]> contagens = new HashMap<>();

        for (Tarefa tarefa : tarefas) {
            if (tarefa.getMateria() == null || tarefa.getMateria().getIdMateria() == null) {
                continue;
            }

            Long materiaId = tarefa.getMateria().getIdMateria();

            long[] contagem = contagens.computeIfAbsent(materiaId, id -> new long[2]);

            contagem[0]++;

            if (Boolean.TRUE.equals(tarefa.getConcluida())) {
                contagem[1]++;
            }
        }

        Map<Long, Double> resultado = new HashMap<>();

        for (Materia materia : materias) {
            Long materiaId = materia.getIdMateria();
            long[] contagem = contagens.get(materiaId);

            double progresso = contagem == null ? 0.0 : percentual(contagem[1], contagem[0]);

            resultado.put(materiaId, progresso);
        }

        return resultado;
    }

    public double getProgressoTempoMateria(Long materiaId, int mes, int ano) {
        Objects.requireNonNull(materiaId, "O ID da matéria não pode ser nulo");

        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês deve ter entre 1 e 12.");
        }

        if (ano < 1) {
            throw new IllegalArgumentException("O ano deve ser válido.");
        }

        var registros = desempenhoRepository.findByMateriaIdMateriaAndMesAndAno(materiaId, mes, ano);

        long horasConcluidas = registros.stream()
                .mapToLong(registro -> registro.getHorasConcluidas() == null
                                ? 0L
                                : registro.getHorasConcluidas()
                )
                .sum();

        long horasMeta = registros.stream()
                .mapToLong(registro -> registro.getHorasMeta() == null
                                ? 0L
                                : registro.getHorasMeta()
                )
                .sum();

        if (horasMeta <= 0) {
            return 0.0;
        }

        return percentual(horasConcluidas, horasMeta);
    }
}
