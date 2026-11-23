package br.com.giovannapugliesi.agenda.exceptions;

import org.springframework.http.HttpStatus;

/** Credenciais inválidas. Responde 401. */
public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
