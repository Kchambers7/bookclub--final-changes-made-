package com.codingdojo.mvc.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.codingdojo.mvc.models.User;

public interface UserRepository extends CrudRepository<User, Long>{
	
	Optional<User> findByEmail(String email);
    
    Optional<User> findByUserName(String userName);
}