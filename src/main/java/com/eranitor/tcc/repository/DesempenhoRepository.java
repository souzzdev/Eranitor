package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Desempenho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesempenhoRepository
        extends JpaRepository<Desempenho, Long> {

    List<Desempenho> findByMateriaIdMateriaAndMesAndAno(
            Long materiaId,
            Integer mes,
            Integer ano
    );
}