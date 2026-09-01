package com.monsterbank.ms_gerente.gerente;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GerenteService {

    private final GerenteRepository gerenteRepository;

    GerenteService(GerenteRepository gerenteRepository){
        this.gerenteRepository = gerenteRepository;
    }

    public List<GerenteEntity> listar(){
        return gerenteRepository.findAll();
    }

}
