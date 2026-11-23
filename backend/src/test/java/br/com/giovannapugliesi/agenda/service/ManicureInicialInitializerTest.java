package br.com.giovannapugliesi.agenda.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.giovannapugliesi.agenda.model.Perfil;
import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ManicureInicialInitializerTest {

    @Mock UsuarioRepository repository;
    @Mock PasswordEncoder encoder;

    private ManicureInicialInitializer criar(String nome, String telefone, String senha) {
        return new ManicureInicialInitializer(repository, encoder, nome, telefone, senha);
    }

    @Test
    void criaManicureQuandoNaoExisteEVariaveisEstaoDefinidas() {
        when(repository.existsByPerfil(Perfil.MANICURE)).thenReturn(false);
        when(repository.existsByTelefone("5582988887777")).thenReturn(false);
        when(encoder.encode("senhaforte1")).thenReturn("hash");

        criar("Giovanna", "(82) 98888-7777", "senhaforte1").criarSeNecessario();

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPerfil()).isEqualTo(Perfil.MANICURE);
        assertThat(captor.getValue().getTelefone()).isEqualTo("5582988887777");
        assertThat(captor.getValue().getSenhaHash()).isEqualTo("hash");
    }

    @Test
    void naoFazNadaQuandoJaExisteManicure() {
        when(repository.existsByPerfil(Perfil.MANICURE)).thenReturn(true);

        criar("Giovanna", "82988887777", "senhaforte1").criarSeNecessario();

        verify(repository, never()).save(any());
    }

    @Test
    void naoCriaSemVariaveisOuComSenhaCurta() {
        when(repository.existsByPerfil(Perfil.MANICURE)).thenReturn(false);

        criar("", "", "").criarSeNecessario();
        criar("Giovanna", "82988887777", "curta").criarSeNecessario();

        verify(repository, never()).save(any());
    }
}
