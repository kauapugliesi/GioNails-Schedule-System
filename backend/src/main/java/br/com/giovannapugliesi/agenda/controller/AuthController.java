package br.com.giovannapugliesi.agenda.controller;

import br.com.giovannapugliesi.agenda.dto.AuthResponseDTO;
import br.com.giovannapugliesi.agenda.dto.LoginRequestDTO;
import br.com.giovannapugliesi.agenda.dto.RegistroRequestDTO;
import br.com.giovannapugliesi.agenda.dto.UsuarioDTO;
import br.com.giovannapugliesi.agenda.mapper.UsuarioMapper;
import br.com.giovannapugliesi.agenda.security.UsuarioPrincipal;
import br.com.giovannapugliesi.agenda.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioMapper mapper;

    public AuthController(AuthService authService, UsuarioMapper mapper) {
        this.authService = authService;
        this.mapper = mapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(authService.registrar(dto)));
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO dto) {
        return mapper.toDTO(authService.login(dto));
    }

    @GetMapping("/me")
    public UsuarioDTO me(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return mapper.toDTO(authService.buscarUsuario(principal.id()));
    }
}
