package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.AuthenticationDTO;
import com.eranitor.tcc.dto.LoginResponseDTO;
import com.eranitor.tcc.dto.RegisterDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.infra.security.TokenService;
import com.eranitor.tcc.mapper.UsuarioMapper;
import com.eranitor.tcc.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthorizationService service;

    @Test
    void login_deveRetornarTokenQuandoCredenciaisValidas() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("guilherme@exemplo.com");

        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenService.generateToken(usuario)).thenReturn("token-abc");

        LoginResponseDTO login = service.login(new AuthenticationDTO("guilherme@exemplo.com", "SenhaForte@123"));

        assertNotNull(login);
        assertEquals("token-abc", login.token());
    }

    @Test
    void register_deveLancarQuandoSenhasNaoCoincidem() {
        RegisterDTO dto = new RegisterDTO("guilherme@exemplo.com", "SenhaForte@123", "SenhaDiferente@123", "Guilherme", "ETEC", "1234");

        assertThrows(IllegalArgumentException.class, () -> service.register(dto));
    }

    @Test
    void register_deveSalvarUsuarioQuandoDadosValidos() {
        RegisterDTO dto = new RegisterDTO("guilherme@exemplo.com", "SenhaForte@123", "SenhaForte@123", "Guilherme", "ETEC", "1234");

        when(repository.existsByEmail("guilherme@exemplo.com")).thenReturn(false);
        when(usuarioMapper.toEntity(dto)).thenReturn(new Usuario());
        when(passwordEncoder.encode("SenhaForte@123")).thenReturn("hash");

        service.register(dto);

        verify(repository).save(any(Usuario.class));
    }

    @Test
    void validateToken_deveLancarQuandoTokenNaoFornecido() {
        assertThrows(RuntimeException.class, () -> service.validateToken(null));
    }

    @Test
    void refreshToken_deveRetornarNovoToken() {
        when(tokenService.validateTokenForRefresh("token-abc")).thenReturn("guilherme@exemplo.com");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("guilherme@exemplo.com");

        when(repository.findByEmail("guilherme@exemplo.com")).thenReturn(Optional.of(usuario));
        when(tokenService.generateToken(usuario)).thenReturn("novo-token");

        String novoToken = service.refreshToken("Bearer token-abc");

        assertEquals("novo-token", novoToken);
    }

    @Test
    void refreshToken_deveLancarQuandoTokenInvalido() {
        when(tokenService.validateTokenForRefresh("token-invalido")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.refreshToken("Bearer token-invalido"));
    }
}
