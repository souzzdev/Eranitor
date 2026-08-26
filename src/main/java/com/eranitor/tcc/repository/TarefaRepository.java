package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByUsuarioIdUsuarioAndDataVencimentoGreaterThanEqual(
            Long usuarioId, LocalDate data
    );

    List<Tarefa> findByUsuarioIdUsuarioAndConcluida(
            Long usuarioId, Boolean concluida
    );

    Long countByMateriaIdMateriaAndConcluida(
            Long materiaId, Boolean concluida
    );

    List<Tarefa> findByMateriaIdMateria(
            Long materiaId
    );

    List<Tarefa> findByUsuarioIdUsuario(
            Long usuarioId
    );
}
