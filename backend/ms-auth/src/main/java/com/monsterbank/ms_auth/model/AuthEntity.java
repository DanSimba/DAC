package com.monsterbank.ms_auth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.monsterbank.ms_auth.enumeration.UserType;

@Document(collection = "usuarios")
public class AuthEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String login;

    private String cpf;
    private String senha;
    private UserType tipo;
    private boolean ativo;

    public AuthEntity() {}

    public AuthEntity(String login, String cpf, String senha, UserType tipo) {
        this.login = login;
        this.cpf = cpf;
        this.senha = senha;
        this.tipo = tipo;
        this.ativo = true;
    }

    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getCpf() { return cpf; }
    public String getSenha() { return senha; }
    public UserType getTipo() { return tipo; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}