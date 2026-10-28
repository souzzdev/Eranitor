package com.eranitor.tcc.services;

import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Tarefa;
import com.eranitor.tcc.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PriorizacaoService {

    private final TarefaRepository tarefaRepository;
    private final ProgressoService progressoService;

    public PriorizacaoService(TarefaRepository tarefaRepository, ProgressoService progressoService) {
        this.tarefaRepository = tarefaRepository;
        this.progressoService = progressoService;
    }

    public double calcularAtraso(LocalDate dataVencimento, boolean concluida) {
        if (concluida || dataVencimento == null){
            return 0;
        }

        LocalDate hoje = LocalDate.now();

        if (hoje.isAfter(dataVencimento)){
            return ChronoUnit.DAYS.between(dataVencimento, hoje);
        }
        return 0;
    }

    public double calcularProximidade(LocalDate dataVencimento) {
        if (dataVencimento == null){
            return 0;
        }

        LocalDate hoje = LocalDate.now();
        long diasAteVencimento = ChronoUnit.DAYS.between(hoje, dataVencimento);

        if (diasAteVencimento < 0){
            return 0;
        } else if (diasAteVencimento <= 3) {
            return 3.0;
        }  else if (diasAteVencimento <= 7) {
            return 2.0;
        } else {
            return 1.0;
        }
    }

    public double calcularPrioridade(Tarefa tarefa, double progressoMateria) {
        Objects.requireNonNull(tarefa, "A tarefa não poder ser nula.");
        Materia materia = Objects.requireNonNull(tarefa.getMateria(), "A tarefa deve possuir uma matéria.");
        Integer dificuldade = Objects.requireNonNull(materia.getDificuldade(), "A matéria deve possuir uma dificuldade definida.");

        if (progressoMateria < 0 || progressoMateria > 1) {
            throw new IllegalArgumentException("O progresso deve estar entre 0 e 1");
        }

        double atraso = calcularAtraso(tarefa.getDataVencimento(), Boolean.TRUE.equals(tarefa.getConcluida()));
        double urgencia = calcularProximidade(tarefa.getDataVencimento());

        double dificuldadePonderada = 2.0 * (dificuldade - 1);

        return (atraso * 5) + (urgencia * 3) + dificuldadePonderada - (progressoMateria * 2);
    }

    private List<Tarefa> buscarTarefasDoUsuario(Long usuarioId) {
        Objects.requireNonNull(usuarioId, "O ID do usuário não pode ser nulo.");

        return tarefaRepository.findByUsuarioIdUsuario(usuarioId);
    }

    public List<Tarefa> getTarefasOrdenadas(Long usuarioId) {
        List<Tarefa> tarefas = buscarTarefasDoUsuario(usuarioId);

        Map<Long, Double> progressoPorMateria = progressoService.getProgressoPorMateria(usuarioId);

        return tarefas.stream()
                .filter(tarefa -> !Boolean.TRUE.equals(tarefa.getConcluida()))
                .filter(tarefa -> tarefa.getMateria() != null)
                .filter(tarefa -> tarefa.getMateria().getIdMateria() != null)
                .sorted(Comparator.comparingDouble((Tarefa tarefa) -> {
                    Long materiaId = tarefa.getMateria().getIdMateria();

                    double progressoPercentual = progressoPorMateria
                            .getOrDefault(materiaId, 0.0);

                    double progresso = progressoPercentual / 100.0;

                    return calcularPrioridade(tarefa, progresso);
                }).reversed())
                .toList();
    }

    public List<Tarefa> getTarefasProximas(Long usuarioId, int dias) {
        Objects.requireNonNull(
                usuarioId,
                "O ID do usuário não pode ser nulo."
        );

        if (dias < 0) {
            throw new IllegalArgumentException(
                    "O número de dias não pode ser negativo."
            );
        }

        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);

        return buscarTarefasDoUsuario(usuarioId).stream()
                .filter(tarefa -> !Boolean.TRUE.equals(tarefa.getConcluida()))
                .filter(tarefa -> tarefa.getDataVencimento() != null)
                .filter(tarefa -> {
                    LocalDate vencimento = tarefa.getDataVencimento();

                    return !vencimento.isBefore(hoje)
                            && !vencimento.isAfter(limite);
                })
                .sorted(Comparator.comparing(Tarefa::getDataVencimento))
                .toList();
    }
}
