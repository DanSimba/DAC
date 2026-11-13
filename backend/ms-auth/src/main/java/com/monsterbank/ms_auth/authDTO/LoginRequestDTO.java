package com.monsterbank.ms_auth.authDTO;

public record LoginRequestDTO(
    String email, 
    String senha
) {}