package com.searchplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.searchplace.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByUserId(String userId);		
}
