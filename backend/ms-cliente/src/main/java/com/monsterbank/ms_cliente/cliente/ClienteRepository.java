package com.monsterbank.ms_cliente.cliente;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, String> {

    Optional<List<ClienteEntity>> findTop50ByOrderByNomeDesc();

    Optional<ClienteEntity> findByCpf(String cpf);
    Optional<List<ClienteEntity>> findByCpfContaining(String cpf);
    Optional<List<ClienteEntity>> findByNomeContaining(String nome);
}
