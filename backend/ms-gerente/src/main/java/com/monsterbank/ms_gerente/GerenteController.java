package com.monsterbank.ms_gerente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monsterbank.ms_gerente.aprovacao.AprovacaoService;
import com.monsterbank.ms_gerente.gerente.GerenteDTO;
import com.monsterbank.ms_gerente.gerente.GerenteEntity;
import com.monsterbank.ms_gerente.gerente.GerenteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/")
public class GerenteController {

    private final GerenteService gerenteService;
    //private final  AprovacaoService aprovacaoService;

    GerenteController(GerenteService gerenteService, AprovacaoService aprovacaoService) {
        this.gerenteService = gerenteService;
        //this.aprovacaoService = aprovacaoService;
    }

    @GetMapping
    public ResponseEntity<List<GerenteDTO>> listar(){
        return ResponseEntity.ok(gerenteService.listar());
    };

    @GetMapping("/{id}")
    public ResponseEntity<GerenteDTO> buscaId(@PathVariable Long id) {
        return ResponseEntity.ok(gerenteService.buscaId(id));
    }
    
    @PostMapping 
    public ResponseEntity<GerenteDTO> criar(@RequestBody GerenteDTO gerenteDTO) {
        GerenteDTO gerente = gerenteService.salvar(gerenteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(gerente);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GerenteDTO> atualizar(@PathVariable Long id, @RequestBody GerenteDTO gerenteDTO) {
        return ResponseEntity.ok(gerenteService.atualizar(id,gerenteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        gerenteService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
