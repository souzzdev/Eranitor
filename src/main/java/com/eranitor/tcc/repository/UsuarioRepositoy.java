package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepositoy extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

}