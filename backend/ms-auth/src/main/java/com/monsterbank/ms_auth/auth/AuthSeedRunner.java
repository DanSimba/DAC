package com.monsterbank.ms_auth.auth;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class AuthSeedRunner implements ApplicationRunner {
    private final AuthSeedService authSeedService;

    public AuthSeedRunner(AuthSeedService authSeedService) {
        this.authSeedService = authSeedService;
    }

    @Override
    public void run(ApplicationArguments args) {
        authSeedService.recriarDados();
    }
}
