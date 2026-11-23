package br.com.giovannapugliesi.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "Informe seu telefone.")
        @Size(max = 25, message = "Telefone inválido.")
        String telefone,

        @NotBlank(message = "Informe sua senha.")
        @Size(max = 72, message = "Senha inválida.")
        String senha) {

    @Override
    public String toString() {
        return "LoginRequestDTO[telefone=" + telefone + ", senha=***]";
    }
}
