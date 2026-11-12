package com.eranitor.tcc.infra.security;

import com.eranitor.tcc.dto.ErrorResponseDTO;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);

    private final TokenService tokenService;
    private final UsuarioRepository repository;
    private final ObjectMapper objectMapper;

    public SecurityFilter(
            TokenService tokenService,
            UsuarioRepository repository,
            ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = recoveryToken(request);

        if (token != null) {

            String login = tokenService.validateToken(token);

            if (login != null) {

                Optional<Usuario> usuario;

                try {
                    usuario = repository.findByEmail(login);
                } catch (RuntimeException e) {
                    // Sem banco não dá para resolver o dono do token. Isso é
                    // indisponibilidade do serviço, não credencial inválida:
                    // responder 503 evita transformar queda de banco em 401.
                    log.error(
                            "Falha ao carregar o usuário do token ({}): {}",
                            login,
                            e.getMessage(),
                            e
                    );

                    responderIndisponivel(response);
                    return;
                }

                usuario.ifPresent(this::autenticar);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void autenticar(Usuario usuario) {
        var authentication = new UsernamePasswordAuthenticationToken(
                usuario,
                null,
                usuario.getAuthorities()
        );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    private void responderIndisponivel(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", "5");

        objectMapper.writeValue(response.getWriter(), new ErrorResponseDTO(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Serviço indisponível no momento. Tente novamente em instantes."
        ));
    }

    private String recoveryToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }
}
