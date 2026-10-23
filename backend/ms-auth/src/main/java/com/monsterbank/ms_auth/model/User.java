package com.monsterbank.ms_auth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document 
@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class User {
  
    @Id
    private Integer userId;
    private String cpf;
    private String userType;
    private String email;
    private String password;
    private Boolean active;

}
