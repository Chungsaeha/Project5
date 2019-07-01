package com.searchplace.service;

import com.searchplace.entity.User;

public interface UserService {
	
	public void signupUser(User user);
	public User findOne(String userId);
}
