package br.com.giovannapugliesi.agenda.exceptions;

import org.springframework.http.HttpStatus;

/** Violação de regra de negócio. Por padrão responde 422. */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    public BusinessException(String message) {
        this(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
