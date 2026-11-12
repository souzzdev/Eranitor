package com.eranitor.tcc.infra.exception;

/**
 * Lançada quando o usuário autenticado tenta acessar ou alterar
 * um recurso que pertence a outro usuário.
 */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String message) {
        super(message);
    }
}
