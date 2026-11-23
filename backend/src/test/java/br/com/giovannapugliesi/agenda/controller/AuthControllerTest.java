package br.com.giovannapugliesi.agenda.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.giovannapugliesi.agenda.exceptions.ConflictException;
import br.com.giovannapugliesi.agenda.exceptions.UnauthorizedException;
import br.com.giovannapugliesi.agenda.mapper.UsuarioMapper;
import br.com.giovannapugliesi.agenda.model.Perfil;
import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.repository.UsuarioRepository;
import br.com.giovannapugliesi.agenda.security.ApiErrorWriter;
import br.com.giovannapugliesi.agenda.security.CorsConfig;
import br.com.giovannapugliesi.agenda.security.JwtService;
import br.com.giovannapugliesi.agenda.security.RestAccessDeniedHandler;
import br.com.giovannapugliesi.agenda.security.RestAuthenticationEntryPoint;
import br.com.giovannapugliesi.agenda.security.SecurityConfig;
import br.com.giovannapugliesi.agenda.service.Autenticacao;
import br.com.giovannapugliesi.agenda.service.AuthService;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CorsConfig.class, ApiErrorWriter.class, RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class, JwtService.class, UsuarioMapper.class})
@TestPropertySource(properties = {
        "app.jwt.secret=segredo-de-teste-com-mais-de-32-caracteres-ok",
        "app.jwt.expiration-minutes=60",
        "app.security.bcrypt-strength=4",
        "app.cors.allowed-origins=http://localhost:3000"
})
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;
    @MockBean AuthService authService;
    @MockBean UsuarioRepository usuarioRepository;

    private Usuario usuario(UUID id) {
        Usuario u = new Usuario("Maria Souza", "5582999999999", "hash", Perfil.CLIENTE);
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }

    @Test
    void registrarValidoRetorna201ComTokenESemSenha() throws Exception {
        UUID id = UUID.randomUUID();
        when(authService.registrar(any())).thenReturn(new Autenticacao("tok", Instant.now().plusSeconds(60), usuario(id)));

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Maria Souza\",\"telefone\":\"(82) 99999-9999\",\"senha\":\"senha1234\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("tok"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.perfil").value("CLIENTE"))
                .andExpect(jsonPath("$.usuario.senhaHash").doesNotExist())
                .andExpect(jsonPath("$.usuario.senha").doesNotExist());
    }

    @Test
    void registrarInvalidoRetorna400ComViolacoes() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"telefone\":\"abc\",\"senha\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.violations").isNotEmpty());
    }

    @Test
    void registrarComTelefoneExistenteRetorna409() throws Exception {
        when(authService.registrar(any())).thenThrow(new ConflictException("Este telefone já está cadastrado."));

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Maria\",\"telefone\":\"82999999999\",\"senha\":\"senha1234\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Este telefone já está cadastrado."));
    }

    @Test
    void loginComCredenciaisInvalidasRetorna401Padronizado() throws Exception {
        when(authService.login(any())).thenThrow(new UnauthorizedException("Telefone ou senha inválidos."));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telefone\":\"82999999999\",\"senha\":\"errada123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Telefone ou senha inválidos."));
    }

    @Test
    void meSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void meComTokenValidoRetornaOProprioUsuario() throws Exception {
        UUID id = UUID.randomUUID();
        Usuario u = usuario(id);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));
        when(authService.buscarUsuario(id)).thenReturn(u);
        String token = jwtService.gerar(id, Perfil.CLIENTE).token();

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.perfil").value("CLIENTE"));
    }
}
