package com.example.demo.services.peopleRegisterService;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.constcomponent.messengerEnums.Roles;

import java.util.List;

public interface UserRepositoryService {
	public List<User> findAll();

	public User findOnebyRId(Long id);

	public User findTop();

	public void saveAndFlush(User user);

	public void remove(User user);

	public List<User> getByRole(Roles roles);
}
