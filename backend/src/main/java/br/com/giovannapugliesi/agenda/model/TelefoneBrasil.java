package br.com.giovannapugliesi.agenda.model;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Normaliza telefones brasileiros para o formato armazenado: somente dígitos com DDI 55
 * (ex.: "(82) 99999-9999" -> "5582999999999").
 */
public final class TelefoneBrasil {

    private static final Pattern CARACTERES_PERMITIDOS = Pattern.compile("^[0-9()+\\-.\\s]+$");

    private TelefoneBrasil() {
    }

    public static Optional<String> normalizar(String entrada) {
        if (entrada == null || !CARACTERES_PERMITIDOS.matcher(entrada).matches()) {
            return Optional.empty();
        }
        String digitos = entrada.replaceAll("\\D", "");

        // remove o DDI 55 quando presente (55 + 10 ou 11 dígitos)
        if (digitos.startsWith("55") && (digitos.length() == 12 || digitos.length() == 13)) {
            digitos = digitos.substring(2);
        }
        if (digitos.length() != 10 && digitos.length() != 11) {
            return Optional.empty();
        }
        // DDD de 11 a 99 (sem zero à esquerda)
        if (digitos.charAt(0) == '0' || digitos.charAt(1) == '0') {
            return Optional.empty();
        }
        // celular com 11 dígitos começa com 9 após o DDD
        if (digitos.length() == 11 && digitos.charAt(2) != '9') {
            return Optional.empty();
        }
        return Optional.of("55" + digitos);
    }
}
