package com.saurabh.dp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.saurabh.dp.model.*;



// Communicating with the database

public interface UserRepository extends JpaRepository<UserDtls, Integer> {
  
	public boolean existsByEmail(String email);
	
	public UserDtls findByEmail(String email);
}
