package com.monsterbank.ms_auth.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.monsterbank.ms_auth.model.AuthEntity;
import com.monsterbank.ms_auth.model.User;
import java.util.List;


public interface UserRepository extends MongoRepository<AuthEntity, String>{
    AuthEntity findByLogin(String login);
}
