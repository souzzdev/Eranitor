package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Integer>
{
    List<Agenda> findByUsuarioId(Long usuarioId);
}
