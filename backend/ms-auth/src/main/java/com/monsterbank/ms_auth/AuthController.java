package com.monsterbank.ms_auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monsterbank.ms_auth.Repository.UserRepository;
import com.monsterbank.ms_auth.auth.AuthService;
import com.monsterbank.ms_auth.authDTO.ErrorDTO;
import com.monsterbank.ms_auth.authDTO.LoginRequestDTO;
import com.monsterbank.ms_auth.authDTO.LoginResponseDTO;
import com.monsterbank.ms_auth.model.AuthEntity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth") 
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final UserRepository userRepository;

    AuthController(AuthService authService,UserRepository userRepository){
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO body) {
        log.info("{} --- Iniciando login para o e-mail: {}", AuthController.class, body.email());
        try {
            authService.login(body.email(), body.senha());
            AuthEntity user = userRepository.findByLogin(body.email());
            LoginResponseDTO response = new LoginResponseDTO(
                user.getCpf(), 
                user.getTipo().toString()
            );
    
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new ErrorDTO(e.getMessage()));
        }
    }
}
