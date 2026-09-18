package com.monsterbank.ms_account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //POR ENQUANTO PERMITE TUDO PQ AINDA ESTÁ EM DESENVOLVIMENTO
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/account/*").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
