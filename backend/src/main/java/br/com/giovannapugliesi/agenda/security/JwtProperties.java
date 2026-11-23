package br.com.giovannapugliesi.agenda.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank(message = "Defina a variável de ambiente JWT_SECRET.") String secret,
        @Min(1) long expirationMinutes) {
}
