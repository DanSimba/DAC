package com.monsterbank.ms_auth.auth;

import java.util.UUID;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.monsterbank.ms_auth.Repository.UserRepository;
import com.monsterbank.ms_auth.model.AuthEntity;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final Argon2PasswordEncoder argon2Encoder;
    
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        
        // Configuração padrão recomendada do Argon2 (salt=16, hash=32, parallelism=1, memory=16384, iterations=2)
        this.argon2Encoder = new Argon2PasswordEncoder(16, 32, 1, 16384, 2);
    }

    public String login(String email, String password){
        return "";
    }

    public String criarUsuarioCliente(String email, String cpf) {
        String senhaProvisoria = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String senhaHash = argon2Encoder.encode(senhaProvisoria);
        AuthEntity novoUsuario = new AuthEntity(email, cpf, senhaHash, "CLIENTE");

        try {
            userRepository.save(novoUsuario);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("E-MAIL_JA_CADASTRADO");
        }

        return senhaProvisoria;
    }
}
