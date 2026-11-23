package br.com.giovannapugliesi.agenda.dto;

import java.time.Instant;

public record AuthResponseDTO(String token, String tipo, Instant expiraEm, UsuarioDTO usuario) {
}
