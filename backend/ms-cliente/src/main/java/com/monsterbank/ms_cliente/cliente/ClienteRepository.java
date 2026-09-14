package com.monsterbank.ms_cliente.cliente;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {

    Optional<List<ClienteEntity>> findTop50ByOrderByNomeDesc();

    Optional<ClienteEntity> findByCpf(String cpf);
    Page<ClienteEntity> findByCpfContainingOrNomeContaining(
            String cpf,
            String nome,
            Pageable pageable
    );
}
