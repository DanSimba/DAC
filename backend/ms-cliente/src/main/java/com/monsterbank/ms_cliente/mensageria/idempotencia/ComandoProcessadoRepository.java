package com.monsterbank.ms_cliente.mensageria.idempotencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComandoProcessadoRepository extends JpaRepository<ComandoProcessadoEntity, String> {
}
