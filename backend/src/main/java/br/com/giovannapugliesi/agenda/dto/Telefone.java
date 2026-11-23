package br.com.giovannapugliesi.agenda.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Valida telefone brasileiro (DDD + número), aceitando máscara. */
@Documented
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Telefone {

    String message() default "Telefone inválido. Use DDD + número, ex.: (82) 99999-9999.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
