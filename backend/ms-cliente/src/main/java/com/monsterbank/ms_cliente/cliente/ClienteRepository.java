package com.monsterbank.ms_cliente.cliente;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {

    Optional<ClienteEntity> findByCpf(String cpf);
    List<ClienteEntity> findByCpfContainingOrNomeContaining(
            String cpf,
            String nome,
            Sort sort
    );
}
