package com.monsterbank.ms_gerente.gerente;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GerenteRepository extends JpaRepository<GerenteEntity, Long> {

    Optional<GerenteEntity> findFirstByOrderByQuantidadeClientesVinculadosAsc();
    
}
