package br.com.giovannapugliesi.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequestDTO(
        @NotBlank(message = "Informe seu nome.")
        @Size(min = 2, max = 120, message = "O nome deve ter entre 2 e 120 caracteres.")
        String nome,

        @NotBlank(message = "Informe seu telefone.")
        @Telefone
        String telefone,

        @NotBlank(message = "Crie uma senha.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
        String senha) {

    /** Nunca expõe a senha em logs. */
    @Override
    public String toString() {
        return "RegistroRequestDTO[nome=" + nome + ", telefone=" + telefone + ", senha=***]";
    }
}
