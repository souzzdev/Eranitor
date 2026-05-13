package com.eranitor.tcc.repository;

import com.eranitor.tcc.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositoy extends JpaRepository<Usuario, Long> {
    Usuario findByLogin(String login);
}
