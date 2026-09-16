package com.eranitor.tcc.services;

import com.eranitor.tcc.entity.Tarefa;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PriorizacaoService {

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
        double atraso = calcularAtraso(tarefa.getDataVencimento(), Boolean.TRUE.equals(tarefa.getConcluida()));
        double proximidade = calcularProximidade(tarefa.getDataVencimento());

        return (atraso * 5) + (proximidade * 3) - (progressoMateria * 2);
    }


}
