package br.com.giovannapugliesi.agenda.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.giovannapugliesi.agenda.mapper.UsuarioMapper;
import br.com.giovannapugliesi.agenda.model.Perfil;
import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.repository.UsuarioRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Verifica a cadeia de segurança real (filtro JWT + regras por perfil) com rotas de teste. */
@WebMvcTest(controllers = SecurityRulesTest.Sonda.class)
@Import({SecurityConfig.class, CorsConfig.class, ApiErrorWriter.class, RestAuthenticationEntryPoint.class,
        RestAccessDeniedHandler.class, JwtService.class, UsuarioMapper.class, SecurityRulesTest.Sonda.class})
@TestPropertySource(properties = {
        "app.jwt.secret=segredo-de-teste-com-mais-de-32-caracteres-ok",
        "app.jwt.expiration-minutes=60",
        "app.security.bcrypt-strength=4",
        "app.cors.allowed-origins=http://localhost:3000"
})
class SecurityRulesTest {

    @RestController
    static class Sonda {
        @GetMapping("/api/admin/sonda")
        String admin() {
            return "ok";
        }

        @GetMapping("/api/cliente/sonda")
        String autenticado() {
            return "ok";
        }
    }

    @Autowired MockMvc mockMvc;
    @Autowired JwtService jwtService;
    @MockBean UsuarioRepository usuarioRepository;

    private String tokenDe(Perfil perfil, boolean ativo) {
        UUID id = UUID.randomUUID();
        Usuario u = new Usuario("Fulana", "5582999999999", "hash", perfil);
        ReflectionTestUtils.setField(u, "id", id);
        ReflectionTestUtils.setField(u, "ativo", ativo);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(u));
        return "Bearer " + jwtService.gerar(id, perfil).token();
    }

    @Test
    void rotaAdminSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/admin/sonda")).andExpect(status().isUnauthorized());
    }

    @Test
    void rotaAdminComTokenInvalidoRetorna401() throws Exception {
        mockMvc.perform(get("/api/admin/sonda").header("Authorization", "Bearer lixo"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void clienteNaoAcessaRotaAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/sonda").header("Authorization", tokenDe(Perfil.CLIENTE, true)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void manicureAcessaRotaAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/sonda").header("Authorization", tokenDe(Perfil.MANICURE, true)))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioInativoComTokenValidoEBarrado() throws Exception {
        mockMvc.perform(get("/api/cliente/sonda").header("Authorization", tokenDe(Perfil.CLIENTE, false)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rotasNaoListadasExigemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/cliente/sonda")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/cliente/sonda").header("Authorization", tokenDe(Perfil.CLIENTE, true)))
                .andExpect(status().isOk());
    }

    @Test
    void preflightCorsDaOrigemPermitidaFunciona() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void preflightCorsDeOrigemNaoPermitidaEBloqueado() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", "http://site-malicioso.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
