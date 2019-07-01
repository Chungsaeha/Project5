package com.searchplace.service;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.searchplace.entity.User;
import com.searchplace.repository.UserRepository;

@Service("UserService")
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Override
	@Transactional
	public User findOne(String userId) {
		return repository.findByUserId(userId);
	}

	@Override
	@Transactional
	public void signupUser(User user) {
		repository.save(user);
	}
}
