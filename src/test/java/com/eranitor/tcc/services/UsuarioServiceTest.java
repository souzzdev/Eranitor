package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AlterarEmailDTO;
import com.eranitor.tcc.dto.AlterarSenhaDTO;
import com.eranitor.tcc.dto.AtualizarPerfilDTO;
import com.eranitor.tcc.dto.UsuarioResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.mapper.UsuarioMapper;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.eranitor.tcc.infra.security.OwnershipService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private OwnershipService ownershipService;

    @InjectMocks
    private UsuarioService service;

    @Test
    void findByLogin_deveRetornar404QuandoUsuarioNaoExiste() {
        when(repository.findByEmail("ninguem@exemplo.com")).thenReturn(Optional.empty());

        ResponseEntity<?> resposta = service.findByLogin("ninguem@exemplo.com");

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void findByLogin_deveRetornarUsuarioQuandoExiste() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("guilherme@exemplo.com");

        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Guilherme", "guilherme@exemplo.com", "1234", "ETEC", null, null);

        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(dto);

        ResponseEntity<?> resposta = service.findByLogin("guilherme@exemplo.com");

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
    }

    @Test
    void getPerfil_deveLancarQuandoUsuarioNaoExiste() {
        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getPerfil("guilherme@exemplo.com"));
    }

    @Test
    void getPerfil_deveRetornarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("guilherme@exemplo.com");

        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Guilherme", "guilherme@exemplo.com", "1234", "ETEC", null, null);

        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(dto);

        UsuarioResponseDTO resposta = service.getPerfil("guilherme@exemplo.com");

        assertNotNull(resposta);
        assertEquals(1L, resposta.idUsuario());
    }

    @Test
    void updatePerfil_deveAtualizarENoRetornar() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);

        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.updateEntity(any(AtualizarPerfilDTO.class), any(Usuario.class))).thenReturn(usuario);
        when(repository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponseDTO(usuario)).thenReturn(new UsuarioResponseDTO(1L, "Guilherme", "guilherme@exemplo.com", "1234", "ETEC", null, null));

        UsuarioResponseDTO resposta = service.updatePerfil("guilherme@exemplo.com", new AtualizarPerfilDTO("Guilherme", "ETEC", "1234"));

        assertNotNull(resposta);
        verify(repository).save(usuario);
    }

    @Test
    void alterarEmail_deveLancarQuandoUsuarioNaoExiste() {
        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.alterarEmail("guilherme@exemplo.com", new AlterarEmailDTO("novo@exemplo.com", "SenhaForte@123")));
    }

    @Test
    void alterarEmail_deveLancarQuandoSenhaAtualIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setPassword("hash");

        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaForte@123", "hash")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                service.alterarEmail("guilherme@exemplo.com", new AlterarEmailDTO("novo@exemplo.com", "SenhaForte@123")));
    }

    @Test
    void alterarEmail_deveLancarQuandoNovoEmailJaCadastrado() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setPassword("hash");

        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaForte@123", "hash")).thenReturn(true);
        when(repository.existsByEmail("novo@exemplo.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.alterarEmail("guilherme@exemplo.com", new AlterarEmailDTO("novo@exemplo.com", "SenhaForte@123")));
    }

    @Test
    void alterarEmail_deveAtualizarEmail() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setPassword("hash");
        usuario.setEmail("guilherme@exemplo.com");

        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaForte@123", "hash")).thenReturn(true);
        when(repository.existsByEmail("novo@exemplo.com")).thenReturn(false);

        service.alterarEmail("guilherme@exemplo.com", new AlterarEmailDTO("novo@exemplo.com", "SenhaForte@123"));

        assertEquals("novo@exemplo.com", usuario.getEmail());
        verify(repository).save(usuario);
    }

    @Test
    void alterarSenha_deveLancarQuandoUsuarioNaoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.alterarSenha(1L, new AlterarSenhaDTO("SenhaForte@123", "NovaSenha@123", "NovaSenha@123")));
    }

    @Test
    void alterarSenha_deveLancarQuandoSenhaAtualIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setPassword("hash");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaErrada@123", "hash")).thenReturn(false);

        assertThrows(RuntimeException.class, () ->
                service.alterarSenha(1L, new AlterarSenhaDTO("SenhaErrada@123", "NovaSenha@123", "NovaSenha@123")));
    }

    @Test
    void alterarSenha_deveLancarQuandoNovaSenhaIgualAtual() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setPassword("hash");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaForte@123", "hash")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.alterarSenha(1L, new AlterarSenhaDTO("SenhaForte@123", "SenhaForte@123", "SenhaForte@123")));
    }

    @Test
    void alterarSenha_deveLancarQuandoConfirmacaoIncorreta() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setPassword("hash");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaForte@123", "hash")).thenReturn(true);
        when(passwordEncoder.matches("NovaSenha@123", "hash")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                service.alterarSenha(1L, new AlterarSenhaDTO("SenhaForte@123", "NovaSenha@123", "OutraSenha@123")));
    }

    @Test
    void alterarSenha_deveAlterarSenha() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setPassword("hash");

        when(repository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("SenhaForte@123", "hash")).thenReturn(true);
        when(passwordEncoder.matches("NovaSenha@123", "hash")).thenReturn(false);
        when(passwordEncoder.encode("NovaSenha@123")).thenReturn("hash-novo");

        service.alterarSenha(1L, new AlterarSenhaDTO("SenhaForte@123", "NovaSenha@123", "NovaSenha@123"));

        assertEquals("hash-novo", usuario.getPassword());
        verify(repository).save(usuario);
    }
}
