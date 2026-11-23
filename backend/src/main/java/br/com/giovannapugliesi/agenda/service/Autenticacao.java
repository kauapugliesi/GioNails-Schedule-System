package br.com.giovannapugliesi.agenda.service;

import br.com.giovannapugliesi.agenda.model.Usuario;
import java.time.Instant;

/** Resultado de um login/cadastro: token emitido + usuário. */
public record Autenticacao(String token, Instant expiraEm, Usuario usuario) {
}
