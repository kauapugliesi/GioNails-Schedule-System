package br.com.giovannapugliesi.agenda.exceptions;

import org.springframework.http.HttpStatus;

/** Entrada inválida detectada na camada de serviço. Responde 400. */
public class InvalidInputException extends BusinessException {

    public InvalidInputException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
