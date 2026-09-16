package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    // RN008 - Listar tarefas de um usuário a partir de uma data
    List<Tarefa> findByUsuarioIdUsuarioAndDataVencimentoGreaterThanEqual(
            Long usuarioId,
            LocalDate data
    );

    // RN008 - Listar tarefas por status
    List<Tarefa> findByUsuarioIdUsuarioAndConcluida(
            Long usuarioId,
            Boolean concluida
    );

    // RN004 - Contar tarefas concluídas de uma matéria
    Long countByMateriaIdMateriaAndConcluida(
            Long materiaId,
            Boolean concluida
    );

    // RN004 - Contar todas as tarefas de uma matéria
    Long countByMateriaIdMateria(
            Long materiaId
    );

    // RN008 - Listar tarefas de uma matéria
    List<Tarefa> findByMateriaIdMateria(
            Long materiaId
    );

    // RN008 - Listar tarefas de um tópico
    List<Tarefa> findByTopicoIdTopico(
            Long topicoId
    );

    // RN008 - Listar todas as tarefas do usuário
    List<Tarefa> findByUsuarioIdUsuario(
            Long usuarioId
    );

    // RN008 - Verificar tarefa duplicada dentro da matéria e usuário
    Boolean existsByTituloIgnoreCaseAndMateria_IdMateriaAndUsuario_IdUsuario(
            String titulo,
            Long materiaId,
            Long usuarioId
    );
}
