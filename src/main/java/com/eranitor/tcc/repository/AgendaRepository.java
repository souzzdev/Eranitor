package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Integer>
{
    List<Agenda> findByUsuario_IdUsuario(Long usuarioId);

    List<Agenda> findByUsuario_IdUsuarioAndData_2Between(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim
    );

    List<Agenda> findByUsuario_IdUsuarioAndData_2(
            Long usuarioId,
            LocalDate data
    );

    List<Agenda> findByTipo(String tipo);
}
