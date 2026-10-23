package com.monsterbank.ms_auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monsterbank.ms_auth.Repository.UserRepository;
import com.monsterbank.ms_auth.auth.AuthService;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth") 
public class AuthController {
    
    private final AuthService authService;
    private final UserRepository userRepository;

    AuthController(AuthService authService,UserRepository userRepository){
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String token = authService.login(
            body.get("email"),
            body.get("password"));
        
        return Map.of("token", token);
    }
    

}
