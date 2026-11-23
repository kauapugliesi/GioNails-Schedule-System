package br.com.giovannapugliesi.agenda.mapper;

import br.com.giovannapugliesi.agenda.dto.AuthResponseDTO;
import br.com.giovannapugliesi.agenda.dto.UsuarioDTO;
import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.service.Autenticacao;
import org.springframework.stereotype.Component;

/** Conversão entre model e DTO; invocado pelos controllers. */
@Component
public class UsuarioMapper {

    public UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getTelefone(), usuario.getPerfil());
    }

    public AuthResponseDTO toDTO(Autenticacao autenticacao) {
        return new AuthResponseDTO(autenticacao.token(), "Bearer", autenticacao.expiraEm(),
                toDTO(autenticacao.usuario()));
    }
}
