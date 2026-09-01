package com.monsterbank.ms_gerente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monsterbank.ms_gerente.aprovacao.AprovacaoService;
import com.monsterbank.ms_gerente.gerente.GerenteDTO;
import com.monsterbank.ms_gerente.gerente.GerenteEntity;
import com.monsterbank.ms_gerente.gerente.GerenteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/")
public class GerenteController {

    private final GerenteService gerenteService;
    //private final  AprovacaoService aprovacaoService;

    GerenteController(GerenteService gerenteService, AprovacaoService aprovacaoService) {
        this.gerenteService = gerenteService;
        //this.aprovacaoService = aprovacaoService;
    }

    @GetMapping //Só ta para teste pq ainda estou fazendo a base
    public List<GerenteEntity> listar(){
        return gerenteService.listar();
    };

    @PostMapping //Só ta para teste pq ainda estou fazendo a base
    public String postMethodName(@RequestBody GerenteDTO gerenteDTO) {
        
        return "Retorno do post";
    }
    
}
