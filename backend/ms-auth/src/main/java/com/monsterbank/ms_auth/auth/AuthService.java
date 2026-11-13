package com.monsterbank.ms_auth.auth;

import java.util.UUID;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

// import com.mongodb.DuplicateKeyException;
import org.springframework.dao.DuplicateKeyException;
import com.monsterbank.ms_auth.Repository.UserRepository;
import com.monsterbank.ms_auth.enumeration.UserType;
import com.monsterbank.ms_auth.exception.EmailJaCadastradoException;
import com.monsterbank.ms_auth.exception.UsuarioInativoException;
import com.monsterbank.ms_auth.exception.UsuarioOuSenhaIvalidosException;
import com.monsterbank.ms_auth.model.AuthEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final Argon2PasswordEncoder argon2Encoder;
    
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        
        // Configuração padrão recomendada do Argon2 (salt=16, hash=32, parallelism=1, memory=16384, iterations=2)
        this.argon2Encoder = new Argon2PasswordEncoder(16, 32, 1, 16384, 2);
    }

    public void login(String email, String password){
        log.info("{} --- Iniciando validação de login para: {}", AuthService.class, email);
        AuthEntity usuario = userRepository.findByLogin(email);

        if (usuario == null || !argon2Encoder.matches(password, usuario.getSenha())) {
            log.error("{} --- Usuário: [{}] ou senha inválidos: [{}]", AuthService.class, email, password);
            throw new UsuarioOuSenhaIvalidosException();
        }

        if (!usuario.isAtivo()) {
            log.error("{} --- Usuário: [{}] inativo", AuthService.class, email);
            throw new UsuarioInativoException();
        }
    }

    public String criarUsuarioCliente(String email, String cpf) {
        log.info("{} --- Iniciando criação de usuário para: {}", AuthService.class, cpf);
        
        String senhaProvisoria = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String senhaHash = argon2Encoder.encode(senhaProvisoria);
        AuthEntity novoUsuario = new AuthEntity(email, cpf, senhaHash, UserType.CLIENTE);

        try {
            userRepository.save(novoUsuario);
            log.info("{} --- Usuário criadop para o CPF: {}", AuthService.class, cpf);
        } catch (DuplicateKeyException e) {
            log.error("{} --- E-mail: [{}] já cadastrado", AuthService.class, email);
            throw new EmailJaCadastradoException();
        }

        return senhaProvisoria;
    }
}
