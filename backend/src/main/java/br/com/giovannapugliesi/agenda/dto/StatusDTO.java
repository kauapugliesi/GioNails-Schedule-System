package br.com.giovannapugliesi.agenda.dto;

import java.time.Instant;

/** Resposta simples de verificação de saúde da API. */
public record StatusDTO(String status, String servico, Instant instante) {
}
