package br.com.giovannapugliesi.agenda.security;

import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lê "Authorization: Bearer ..." e autentica a requisição. Perfil e status (ativo) são
 * consultados no banco a cada requisição, então desativações e mudanças de perfil valem na hora.
 * Instanciado pelo SecurityConfig (não é um bean), para rodar apenas dentro da cadeia do Spring Security.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho != null && cabecalho.startsWith(PREFIXO)) {
            String token = cabecalho.substring(PREFIXO.length()).trim();
            jwtService.extrairUsuarioId(token)
                    .flatMap(usuarioRepository::findById)
                    .filter(Usuario::isAtivo)
                    .ifPresent(usuario -> autenticar(usuario, request));
        }
        chain.doFilter(request, response);
    }

    private void autenticar(Usuario usuario, HttpServletRequest request) {
        var autenticacao = new UsernamePasswordAuthenticationToken(
                UsuarioPrincipal.de(usuario), null,
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name())));
        autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(autenticacao);
    }
}
