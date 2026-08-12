package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    Optional<Materia> findByNomeIgnoreCaseAndUsuario_IdUsuario(
            String nome,
            Long usuarioId
    );

    List<Materia> findAllByUsuario_IdUsuario(Long usuarioId);

    List<Materia> findAllByUsuario_IdUsuarioAndAtivaTrue(Long usuarioId);
}