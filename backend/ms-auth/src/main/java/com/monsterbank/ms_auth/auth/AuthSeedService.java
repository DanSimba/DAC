package com.monsterbank.ms_auth.auth;

import java.util.List;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monsterbank.ms_auth.Repository.UserRepository;
import com.monsterbank.ms_auth.enumeration.UserType;
import com.monsterbank.ms_auth.model.AuthEntity;

@Service
public class AuthSeedService {
    private final UserRepository userRepository;
    private final Argon2PasswordEncoder argon2Encoder;

    public AuthSeedService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.argon2Encoder = new Argon2PasswordEncoder(16, 32, 1, 16384, 2);
    }

    public void recriarDados() {
        userRepository.deleteAll();
        userRepository.saveAll(List.of(
            usuario("cli1@bantads.com.br", "12912861012", UserType.CLIENTE),
            usuario("cli2@bantads.com.br", "09506382000", UserType.CLIENTE),
            usuario("cli3@bantads.com.br", "85733854057", UserType.CLIENTE),
            usuario("cli4@bantads.com.br", "58872160006", UserType.CLIENTE),
            usuario("cli5@bantads.com.br", "76179646090", UserType.CLIENTE),
            usuario("ger1@bantads.com.br", "98574307084", UserType.GERENTE),
            usuario("ger2@bantads.com.br", "64065268052", UserType.GERENTE),
            usuario("ger3@bantads.com.br", "23862179060", UserType.GERENTE),
            usuario("ger4@bantads.com.br", "40501740066", UserType.GERENTE)
        ));
    }

    private AuthEntity usuario(String login, String cpf, UserType tipo) {
        return new AuthEntity(login, cpf, argon2Encoder.encode("tads"), tipo);
    }
}
