package com.monsterbank.ms_gerente.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monsterbank.ms_gerente.service.GerenteService;

@RestController
@RequestMapping("/")
public class GerenteController {

    private GerenteService gerenteService;

    public GerenteController(GerenteService gerenteService){
        this.gerenteService = gerenteService;
    }

    @GetMapping("/teste")
    public String gerente(){
        return gerenteService.teste();
    };
}
