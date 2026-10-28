package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.TempoEstudo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TempoEstudoRepository extends JpaRepository<TempoEstudo, Integer> {
    List<TempoEstudo> findByUsuarioId(Long usuarioId);
}
