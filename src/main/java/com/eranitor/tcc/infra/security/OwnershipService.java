package com.eranitor.tcc.infra.security;

import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.infra.exception.AcessoNegadoException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Centraliza a checagem de propriedade (ownership) dos recursos.
 *
 * O {@link SecurityFilter} coloca a entidade {@link Usuario} como principal
 * no SecurityContext. Nenhum endpoint deve confiar no id de usuário vindo da
 * URL: ele precisa ser igual ao usuário do token.
 */
@Component
public class OwnershipService {

    private static final String MENSAGEM_NEGADO =
            "Acesso negado: o recurso pertence a outro usuário.";

    public Usuario usuarioAutenticado() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof Usuario usuario)) {

            throw new AcessoNegadoException(
                    "Acesso negado: usuário não autenticado."
            );
        }

        return usuario;
    }

    public Long idUsuarioAutenticado() {
        return usuarioAutenticado().getIdUsuario();
    }

    public void exigirUsuario(Long usuarioId) {
        Long autenticado = idUsuarioAutenticado();

        if (usuarioId == null || !autenticado.equals(usuarioId)) {
            throw new AcessoNegadoException(MENSAGEM_NEGADO);
        }
    }

    public void exigirDono(Usuario dono) {
        exigirDono(dono == null ? null : dono.getIdUsuario());
    }

    public void exigirDono(Long donoId) {
        Long autenticado = idUsuarioAutenticado();

        if (donoId == null || !autenticado.equals(donoId)) {
            throw new AcessoNegadoException(MENSAGEM_NEGADO);
        }
    }

    public void exigirLogin(String email) {
        String autenticado = usuarioAutenticado().getEmail();

        if (email == null || autenticado == null || !autenticado.equalsIgnoreCase(email)) {
            throw new AcessoNegadoException(MENSAGEM_NEGADO);
        }
    }
}
