package com.monsterbank.ms_gerente.gerente;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GerenteDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;

}
