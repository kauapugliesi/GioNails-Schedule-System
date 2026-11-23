package br.com.giovannapugliesi.agenda.exceptions;

import org.springframework.http.HttpStatus;

/** Conflito (ex.: horário já ocupado, telefone já cadastrado). Responde 409. */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
