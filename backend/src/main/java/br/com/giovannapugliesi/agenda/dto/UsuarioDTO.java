package br.com.giovannapugliesi.agenda.dto;

import br.com.giovannapugliesi.agenda.model.Perfil;
import java.util.UUID;

/** Dados públicos do usuário. Nunca inclui o hash da senha. */
public record UsuarioDTO(UUID id, String nome, String telefone, Perfil perfil) {
}
