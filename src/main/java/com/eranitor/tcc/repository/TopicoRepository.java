package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    Optional<Topico> findByNomeIgnoreCaseAndMateria_IdMateria(
            String nome,
            Long materiaId
    );

    List<Topico> findAllByMateria_IdMateria(Long materiaId);
}
