package br.com.giovannapugliesi.agenda.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.giovannapugliesi.agenda.dto.LoginRequestDTO;
import br.com.giovannapugliesi.agenda.dto.RegistroRequestDTO;
import br.com.giovannapugliesi.agenda.exceptions.ConflictException;
import br.com.giovannapugliesi.agenda.exceptions.InvalidInputException;
import br.com.giovannapugliesi.agenda.exceptions.UnauthorizedException;
import br.com.giovannapugliesi.agenda.model.Perfil;
import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.repository.UsuarioRepository;
import br.com.giovannapugliesi.agenda.security.JwtService;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String TEL = "5582999999999";

    @Mock UsuarioRepository repository;
    @Mock PasswordEncoder encoder;
    @Mock JwtService jwtService;

    AuthService service;

    @BeforeEach
    void setUp() {
        lenient().when(encoder.encode(anyString())).thenReturn("hash");
        lenient().when(jwtService.gerar(any(), any()))
                .thenReturn(new JwtService.TokenEmitido("tok", Instant.now().plusSeconds(60)));
        service = new AuthService(repository, encoder, jwtService);
    }

    private Usuario usuarioSalvo(boolean ativo) {
        Usuario u = new Usuario("Maria", TEL, "hash-da-senha", Perfil.CLIENTE);
        ReflectionTestUtils.setField(u, "ativo", ativo);
        return u;
    }

    @Test
    void registrarCriaClienteComSenhaCriptografada() {
        when(repository.existsByTelefone(TEL)).thenReturn(false);
        when(repository.saveAndFlush(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Autenticacao resultado = service.registrar(
                new RegistroRequestDTO("  Maria Souza ", "(82) 99999-9999", "senha1234"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository).saveAndFlush(captor.capture());
        Usuario salvo = captor.getValue();
        assertThat(salvo.getNome()).isEqualTo("Maria Souza");
        assertThat(salvo.getTelefone()).isEqualTo(TEL);
        assertThat(salvo.getSenhaHash()).isEqualTo("hash").isNotEqualTo("senha1234");
        assertThat(salvo.getPerfil()).isEqualTo(Perfil.CLIENTE);
        assertThat(resultado.token()).isEqualTo("tok");
    }

    @Test
    void registrarComTelefoneJaCadastradoLancaConflito() {
        when(repository.existsByTelefone(TEL)).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(new RegistroRequestDTO("Maria", "82999999999", "senha1234")))
                .isInstanceOf(ConflictException.class);
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void registrarTrataCorridaDeCadastroPelaConstraintDoBanco() {
        when(repository.existsByTelefone(TEL)).thenReturn(false);
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("uk_usuarios_telefone"));

        assertThatThrownBy(() -> service.registrar(new RegistroRequestDTO("Maria", "82999999999", "senha1234")))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void registrarComTelefoneInvalidoLancaErroDeEntrada() {
        assertThatThrownBy(() -> service.registrar(new RegistroRequestDTO("Maria", "123", "senha1234")))
                .isInstanceOf(InvalidInputException.class);
    }

    @Test
    void loginComCredenciaisCorretasEmiteToken() {
        Usuario u = usuarioSalvo(true);
        when(repository.findByTelefone(TEL)).thenReturn(Optional.of(u));
        when(encoder.matches("senha1234", "hash-da-senha")).thenReturn(true);

        Autenticacao resultado = service.login(new LoginRequestDTO("(82) 99999-9999", "senha1234"));

        assertThat(resultado.usuario()).isSameAs(u);
        assertThat(resultado.token()).isEqualTo("tok");
    }

    @Test
    void loginComSenhaErradaFalhaComMensagemGenerica() {
        when(repository.findByTelefone(TEL)).thenReturn(Optional.of(usuarioSalvo(true)));
        when(encoder.matches(anyString(), eq("hash-da-senha"))).thenReturn(false);

        assertThatThrownBy(() -> service.login(new LoginRequestDTO("82999999999", "errada123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Telefone ou senha inválidos.");
    }

    @Test
    void loginComTelefoneInexistenteGastaTempoDeHashEFalhaIgual() {
        when(repository.findByTelefone(TEL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequestDTO("82999999999", "qualquer123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Telefone ou senha inválidos.");
        verify(encoder).matches(eq("qualquer123"), anyString());
    }

    @Test
    void loginDeUsuarioInativoFalha() {
        when(repository.findByTelefone(TEL)).thenReturn(Optional.of(usuarioSalvo(false)));
        when(encoder.matches("senha1234", "hash-da-senha")).thenReturn(true);

        assertThatThrownBy(() -> service.login(new LoginRequestDTO("82999999999", "senha1234")))
                .isInstanceOf(UnauthorizedException.class);
    }
}
