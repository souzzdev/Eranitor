package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.UsuarioResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService service;

    @Test
    void loadUserByUsername_deveRetornarUsuarioQuandoExiste() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("guilherme@exemplo.com");

        when(usuarioRepository.findByEmail("guilherme@exemplo.com")).thenReturn(java.util.Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("guilherme@exemplo.com");

        assertNotNull(userDetails);
        assertEquals("guilherme@exemplo.com", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_deveLancarQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByEmail("ninguem@exemplo.com")).thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("ninguem@exemplo.com"));
    }
}
