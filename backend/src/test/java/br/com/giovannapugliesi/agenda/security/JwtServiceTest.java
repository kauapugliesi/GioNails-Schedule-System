package br.com.giovannapugliesi.agenda.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.giovannapugliesi.agenda.model.Perfil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-mais-de-32-caracteres-ok";

    private final JwtService service = new JwtService(new JwtProperties(SEGREDO, 60));

    @Test
    void geraTokenQueVoltaAoMesmoUsuario() {
        UUID id = UUID.randomUUID();

        JwtService.TokenEmitido emitido = service.gerar(id, Perfil.CLIENTE);

        assertThat(service.extrairUsuarioId(emitido.token())).contains(id);
        assertThat(emitido.expiraEm()).isAfter(Instant.now());
    }

    @Test
    void rejeitaTokenAdulteradoOuLixo() {
        String token = service.gerar(UUID.randomUUID(), Perfil.CLIENTE).token();

        assertThat(service.extrairUsuarioId(token + "x")).isEmpty();
        assertThat(service.extrairUsuarioId("nao-e-um-jwt")).isEmpty();
        assertThat(service.extrairUsuarioId("")).isEmpty();
    }

    @Test
    void rejeitaTokenAssinadoComOutroSegredo() {
        JwtService outro = new JwtService(new JwtProperties("outro-segredo-totalmente-diferente-123456", 60));
        String tokenDeOutro = outro.gerar(UUID.randomUUID(), Perfil.MANICURE).token();

        assertThat(service.extrairUsuarioId(tokenDeOutro)).isEmpty();
    }

    @Test
    void rejeitaTokenExpirado() {
        String expirado = Jwts.builder()
                .issuer(JwtService.ISSUER)
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
                .expiration(Date.from(Instant.now().minusSeconds(3600)))
                .signWith(Keys.hmacShaKeyFor(SEGREDO.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(service.extrairUsuarioId(expirado)).isEmpty();
    }

    @Test
    void recusaSegredoCurto() {
        assertThatThrownBy(() -> new JwtService(new JwtProperties("curto", 60)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }
}
