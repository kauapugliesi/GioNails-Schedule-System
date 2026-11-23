package br.com.giovannapugliesi.agenda.exceptions;

import java.time.Instant;
import java.util.List;

/** Formato padrão de erro devolvido por toda a API. */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldViolation> violations) {

    public record FieldViolation(String field, String message) {
    }
}
