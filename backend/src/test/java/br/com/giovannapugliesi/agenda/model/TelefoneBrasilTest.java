package br.com.giovannapugliesi.agenda.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TelefoneBrasilTest {

    @ParameterizedTest
    @ValueSource(strings = {"(82) 99999-9999", "82999999999", "+55 82 99999-9999", "5582999999999", "82 9 9999 9999"})
    void normalizaCelularParaDigitosComDdi(String entrada) {
        assertThat(TelefoneBrasil.normalizar(entrada)).contains("5582999999999");
    }

    @ParameterizedTest
    @ValueSource(strings = {"(82) 3333-4444"})
    void aceitaFixoDeDezDigitos(String entrada) {
        assertThat(TelefoneBrasil.normalizar(entrada)).contains("558233334444");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "8299999", "(82) 89999-9999", "(02) 99999-9999",
            "82 99999-9999 ramal 2", "123456789012345", "(82) 99999-999x"})
    void rejeitaEntradasInvalidas(String entrada) {
        assertThat(TelefoneBrasil.normalizar(entrada)).isEmpty();
    }

    @org.junit.jupiter.api.Test
    void rejeitaNulo() {
        assertThat(TelefoneBrasil.normalizar(null)).isEmpty();
    }
}
