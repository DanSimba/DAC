package com.monsterbank.ms_auth.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.monsterbank.ms_auth.model.User;

public interface UserRepository extends MongoRepository<User,Integer>{
    
}
