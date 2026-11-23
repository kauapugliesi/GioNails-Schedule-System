package br.com.giovannapugliesi.agenda.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockHttpServletRequest request() {
        return new MockHttpServletRequest("GET", "/api/teste");
    }

    @Test
    void naoEncontradoRetorna404ComCorpoPadronizado() {
        ResponseEntity<ApiError> res = handler.handleBusiness(
                new ResourceNotFoundException("Serviço não encontrado."), request());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().message()).isEqualTo("Serviço não encontrado.");
        assertThat(res.getBody().path()).isEqualTo("/api/teste");
    }

    @Test
    void conflitoRetorna409() {
        ResponseEntity<ApiError> res = handler.handleBusiness(
                new ConflictException("Horário indisponível."), request());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void credenciaisInvalidasRetorna401() {
        ResponseEntity<ApiError> res = handler.handleBusiness(
                new UnauthorizedException("Telefone ou senha inválidos."), request());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void acessoNegadoRetorna403() {
        ResponseEntity<ApiError> res = handler.handleAccessDenied(new AccessDeniedException("x"), request());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void rotaInexistenteRetorna404EnaoErro500() {
        ResponseEntity<ApiError> res = handler.handleNotFound(
                new NoResourceFoundException(HttpMethod.GET, "api/nao-existe"), request());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void erroInesperadoNaoVazaDetalhes() {
        ResponseEntity<ApiError> res = handler.handleUnexpected(
                new IllegalStateException("senha do banco: 123"), request());

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().message()).doesNotContain("senha");
    }
}
