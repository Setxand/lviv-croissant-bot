package com.example.demo.services.peopleRegisterService;

import com.example.demo.entities.peopleRegister.User;
import com.example.demo.enums.messengerEnums.Roles;

import java.util.List;

public interface UserRepositoryService {
	public List<User> findAll();

	public User findOnebyRId(Long id);

	public User findTop();

	public void saveAndFlush(User user);

	public void remove(User user);

	public List<User> getByRole(Roles roles);
}
