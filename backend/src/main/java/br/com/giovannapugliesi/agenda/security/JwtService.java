package br.com.giovannapugliesi.agenda.security;

import br.com.giovannapugliesi.agenda.model.Perfil;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/** Emite e valida tokens JWT assinados (HMAC). O perfil efetivo sempre vem do banco. */
@Service
public class JwtService {

    static final String ISSUER = "gionails-schedule-system";
    private static final int TAMANHO_MINIMO_SEGREDO = 32;

    private final SecretKey chave;
    private final Duration validade;

    public JwtService(JwtProperties props) {
        if (props.secret() == null || props.secret().getBytes(StandardCharsets.UTF_8).length < TAMANHO_MINIMO_SEGREDO) {
            throw new IllegalStateException("JWT_SECRET deve ter ao menos " + TAMANHO_MINIMO_SEGREDO
                    + " caracteres. Gere um com: openssl rand -base64 48");
        }
        this.chave = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
        this.validade = Duration.ofMinutes(props.expirationMinutes());
    }

    public TokenEmitido gerar(UUID usuarioId, Perfil perfil) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(validade);
        String token = Jwts.builder()
                .issuer(ISSUER)
                .subject(usuarioId.toString())
                .claim("perfil", perfil.name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiraEm))
                .signWith(chave)
                .compact();
        return new TokenEmitido(token, expiraEm);
    }

    /** Retorna o id do usuário se o token for válido (assinatura, emissor e expiração). */
    public Optional<UUID> extrairUsuarioId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(chave)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.of(UUID.fromString(subject));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public record TokenEmitido(String token, Instant expiraEm) {
    }
}
