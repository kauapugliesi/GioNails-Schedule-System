package br.com.giovannapugliesi.agenda.service;

import br.com.giovannapugliesi.agenda.model.Perfil;
import br.com.giovannapugliesi.agenda.model.TelefoneBrasil;
import br.com.giovannapugliesi.agenda.model.Usuario;
import br.com.giovannapugliesi.agenda.repository.UsuarioRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria a conta da manicure (administradora) na inicialização, a partir de variáveis de ambiente,
 * caso ainda não exista nenhuma. A senha nunca fica no código nem no repositório.
 */
@Component
public class ManicureInicialInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ManicureInicialInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String nome;
    private final String telefone;
    private final String senha;

    public ManicureInicialInitializer(UsuarioRepository usuarioRepository,
                                      PasswordEncoder passwordEncoder,
                                      @Value("${app.manicure-inicial.nome:}") String nome,
                                      @Value("${app.manicure-inicial.telefone:}") String telefone,
                                      @Value("${app.manicure-inicial.senha:}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.nome = nome;
        this.telefone = telefone;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        criarSeNecessario();
    }

    void criarSeNecessario() {
        if (usuarioRepository.existsByPerfil(Perfil.MANICURE)) {
            return;
        }
        if (nome.isBlank() || telefone.isBlank() || senha.isBlank()) {
            log.warn("Nenhuma manicure cadastrada. Defina MANICURE_NOME, MANICURE_TELEFONE e MANICURE_SENHA "
                    + "para criar a conta administradora.");
            return;
        }
        Optional<String> telefoneNormalizado = TelefoneBrasil.normalizar(telefone);
        if (telefoneNormalizado.isEmpty()) {
            log.error("MANICURE_TELEFONE inválido; conta da manicure não criada.");
            return;
        }
        if (senha.length() < 8 || senha.length() > 72) {
            log.error("MANICURE_SENHA deve ter de 8 a 72 caracteres; conta da manicure não criada.");
            return;
        }
        if (usuarioRepository.existsByTelefone(telefoneNormalizado.get())) {
            log.error("O telefone configurado para a manicure já pertence a outro usuário; conta não criada.");
            return;
        }
        usuarioRepository.save(new Usuario(nome.trim(), telefoneNormalizado.get(),
                passwordEncoder.encode(senha), Perfil.MANICURE));
        log.info("Conta da manicure criada.");
    }
}
