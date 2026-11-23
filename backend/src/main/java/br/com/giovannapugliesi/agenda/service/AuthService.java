package br.com.giovannapugliesi.agenda.service;

import br.com.giovannapugliesi.agenda.dto.LoginRequestDTO;
import br.com.giovannapugliesi.agenda.dto.RegistroRequestDTO;
import br.com.giovannapugliesi.agenda.exceptions.ConflictException;
import br.com.giovannapugliesi.agenda.exceptions.InvalidInputException;
import br.com.giovannapugliesi.agenda.exceptions.ResourceNotFoundException;
import br.com.giovannapugliesi.agenda.exceptions.UnauthorizedException;
import br.com.giovannapugliesi.agenda.model.Perfil;
import br.com.giovannapugliesi.agenda.model.TelefoneBrasil;
import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.repository.UsuarioRepository;
import br.com.giovannapugliesi.agenda.security.JwtService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String MSG_CREDENCIAIS = "Telefone ou senha inválidos.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    /** Hash descartável usado para gastar o mesmo tempo quando o telefone não existe. */
    private final String hashFicticio;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.hashFicticio = passwordEncoder.encode("senha-ficticia-para-equalizar-o-tempo");
    }

    /** Cadastro público: sempre cria perfil CLIENTE. A manicure nunca se cadastra por aqui. */
    @Transactional
    public Autenticacao registrar(RegistroRequestDTO dto) {
        String telefone = TelefoneBrasil.normalizar(dto.telefone())
                .orElseThrow(() -> new InvalidInputException("Telefone inválido."));

        if (usuarioRepository.existsByTelefone(telefone)) {
            throw new ConflictException("Este telefone já está cadastrado. Tente entrar na sua conta.");
        }

        Usuario novo = new Usuario(dto.nome().trim(), telefone, passwordEncoder.encode(dto.senha()), Perfil.CLIENTE);
        try {
            novo = usuarioRepository.saveAndFlush(novo);
        } catch (DataIntegrityViolationException e) {
            // cadastro simultâneo com o mesmo telefone: a constraint única do banco decide
            throw new ConflictException("Este telefone já está cadastrado. Tente entrar na sua conta.");
        }
        return emitir(novo);
    }

    @Transactional(readOnly = true)
    public Autenticacao login(LoginRequestDTO dto) {
        Optional<Usuario> encontrado = TelefoneBrasil.normalizar(dto.telefone())
                .flatMap(usuarioRepository::findByTelefone);

        if (encontrado.isEmpty()) {
            passwordEncoder.matches(dto.senha(), hashFicticio);
            throw new UnauthorizedException(MSG_CREDENCIAIS);
        }

        Usuario usuario = encontrado.get();
        boolean senhaConfere = passwordEncoder.matches(dto.senha(), usuario.getSenhaHash());
        if (!senhaConfere || !usuario.isAtivo()) {
            throw new UnauthorizedException(MSG_CREDENCIAIS);
        }
        return emitir(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }

    private Autenticacao emitir(Usuario usuario) {
        JwtService.TokenEmitido emitido = jwtService.gerar(usuario.getId(), usuario.getPerfil());
        return new Autenticacao(emitido.token(), emitido.expiraEm(), usuario);
    }
}
