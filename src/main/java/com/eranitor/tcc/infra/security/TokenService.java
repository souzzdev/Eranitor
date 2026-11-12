package com.eranitor.tcc.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.eranitor.tcc.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    private static final String ISSUER = "Eranitor";
    private static final String CLAIM_TIPO = "tipo";
    private static final String TIPO_ACESSO = "acesso";
    private static final String TIPO_REFRESH = "refresh";

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private long expirationHours;

    @Value("${api.security.refresh.expiration-days:30}")
    private long refreshExpirationDays;

    @Value("${api.security.refresh.window-days:7}")
    private long refreshWindowDays;

    public String generateToken(Usuario usuario) {
        return gerarToken(usuario, TIPO_ACESSO, Duration.ofHours(expirationHours));
    }

    public String generateRefreshToken(Usuario usuario) {
        return gerarToken(usuario, TIPO_REFRESH, Duration.ofDays(refreshExpirationDays));
    }

    private String gerarToken(Usuario usuario, String tipo, Duration validade) {
        try {
            Instant agora = Instant.now();

            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withClaim(CLAIM_TIPO, tipo)
                    .withIssuedAt(agora)
                    .withExpiresAt(agora.plus(validade))
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    /**
     * Valida um token de acesso. Tokens de refresh são recusados aqui:
     * um refresh token (validade longa) não deve servir como credencial de API.
     */
    public String validateToken(String token) {
        try {
            DecodedJWT jwt = verificar(token);

            if (TIPO_REFRESH.equals(jwt.getClaim(CLAIM_TIPO).asString())) {
                return null;
            }

            return jwt.getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    /**
     * Valida um token para renovação.
     *
     * <ul>
     *   <li>{@code tipo=refresh}: aceito enquanto não expirar (janela absoluta
     *       de {@code api.security.refresh.expiration-days}).</li>
     *   <li>Tokens de acesso e tokens legados (sem o claim {@code tipo}): aceitos
     *       apenas dentro de {@code api.security.refresh.window-days} contados a
     *       partir da expiração. Isso mantém compatibilidade com clientes antigos
     *       sem permitir renovação infinita de um token antigo.</li>
     * </ul>
     */
    public String validateTokenForRefresh(String token) {
        try {
            DecodedJWT jwt = verificar(token);

            if (TIPO_REFRESH.equals(jwt.getClaim(CLAIM_TIPO).asString())) {
                return jwt.getSubject();
            }

            return dentroDaJanelaDeRenovacao(jwt) ? jwt.getSubject() : null;

        } catch (TokenExpiredException exception) {
            DecodedJWT jwt = JWT.decode(token);

            if (TIPO_REFRESH.equals(jwt.getClaim(CLAIM_TIPO).asString())) {
                return null;
            }

            return dentroDaJanelaDeRenovacao(jwt) ? jwt.getSubject() : null;

        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    private boolean dentroDaJanelaDeRenovacao(DecodedJWT jwt) {
        Instant expiracao = jwt.getExpiresAtAsInstant();

        if (expiracao == null) {
            return false;
        }

        return !Instant.now().isAfter(expiracao.plus(Duration.ofDays(refreshWindowDays)));
    }

    private DecodedJWT verificar(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withIssuer(ISSUER)
                .build();

        return verifier.verify(token);
    }
}
