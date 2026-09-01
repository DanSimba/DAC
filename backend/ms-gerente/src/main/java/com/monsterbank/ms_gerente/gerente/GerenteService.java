package com.monsterbank.ms_gerente.gerente;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class GerenteService {

    private final GerenteRepository gerenteRepository;

    GerenteService(GerenteRepository gerenteRepository){
        this.gerenteRepository = gerenteRepository;
    }

    public GerenteDTO paraDTO(GerenteEntity gerente){
        GerenteDTO respostaDTO = new GerenteDTO();
        respostaDTO.setId(gerente.getId());
        respostaDTO.setNome(gerente.getNome());
        respostaDTO.setCpf(gerente.getCpf());
        respostaDTO.setEmail(gerente.getEmail());
        respostaDTO.setTelefone(gerente.getTelefone());
        return respostaDTO;
    }

    public List<GerenteDTO> listar(){
        return gerenteRepository.findAll().stream()
            .filter(GerenteEntity::getAtivo)
            .map(this::paraDTO)
            .collect(Collectors.toList());
    }

    public GerenteDTO buscaId(Long id){
        GerenteEntity gerente = gerenteRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        return paraDTO(gerente);
    }

    public GerenteDTO salvar(GerenteDTO gerenteDTO){
        GerenteEntity gerente = new GerenteEntity();
        
        gerente.setNome(gerenteDTO.getNome());
        gerente.setCpf(gerenteDTO.getCpf());
        gerente.setEmail(gerenteDTO.getEmail());
        gerente.setTelefone(gerenteDTO.getTelefone());
        gerente.setAtivo(true);

        gerenteRepository.save(gerente);

        return paraDTO(gerente);
    }

    public GerenteDTO atualizar(Long id, GerenteDTO gerenteDTO){
        GerenteEntity gerente = gerenteRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));


        //Lembrando que cpf e email não pode ser alterado
        gerente.setNome(gerenteDTO.getNome());
        gerente.setTelefone(gerenteDTO.getTelefone());

        GerenteEntity gerenteAtualizado = gerenteRepository.save(gerente);
        return paraDTO(gerenteAtualizado);
    }

    public void remover(long id){
        GerenteEntity gerente = gerenteRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        gerente.setAtivo(false);
        gerenteRepository.save(gerente);
    }
}
