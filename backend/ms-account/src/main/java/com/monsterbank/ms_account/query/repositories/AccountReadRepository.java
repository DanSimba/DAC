package com.monsterbank.ms_account.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monsterbank.ms_account.query.entities.AccountReadEntity;

@Repository
public interface AccountReadRepository extends JpaRepository<AccountReadEntity, String> {

            //NÃO PRECISA DE NENHUM MÉTODO PERSONALIZADO PQ TD QUE O GATEWAY PRECISA É
            //FINDALL() E FINDBYID() QUE JA VEM DE PADRÃO 
}