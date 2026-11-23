package br.com.giovannapugliesi.agenda.dto;

import br.com.giovannapugliesi.agenda.model.TelefoneBrasil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<Telefone, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        // nulo/vazio é responsabilidade de @NotBlank
        return valor == null || TelefoneBrasil.normalizar(valor).isPresent();
    }
}
