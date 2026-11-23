package br.com.giovannapugliesi.agenda.security;

import br.com.giovannapugliesi.agenda.model.Perfil;
import br.com.giovannapugliesi.agenda.model.Usuario;
import java.util.UUID;

/** Identidade do usuário autenticado na requisição atual. */
public record UsuarioPrincipal(UUID id, String nome, String telefone, Perfil perfil) {

    public static UsuarioPrincipal de(Usuario usuario) {
        return new UsuarioPrincipal(usuario.getId(), usuario.getNome(), usuario.getTelefone(), usuario.getPerfil());
    }
}
