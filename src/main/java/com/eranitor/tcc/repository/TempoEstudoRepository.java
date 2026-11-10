package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.TempoEstudo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TempoEstudoRepository extends JpaRepository<TempoEstudo, Integer> {
    List<TempoEstudo> findByUsuario_IdUsuario(Long usuarioId);
    List<TempoEstudo> findByMateria_IdMateria(Long materiaId);

    List<TempoEstudo> findByMateria_IdMateriaAndMesAndAno(
            Long materiaId,
            Integer mes,
            Integer ano
    );

    List<TempoEstudo> findByUsuario_IdUsuarioAndMesAndAno(
            Long usuarioId,
            Integer mes,
            Integer ano
    );
}