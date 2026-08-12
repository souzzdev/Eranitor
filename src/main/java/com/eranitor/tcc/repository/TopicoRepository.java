package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Integer> {
    Optional<Topico> findByNomeIgnoreCaseAndUsuario_IdMateria(
            String nome,
            Long materiaId
    );
}
