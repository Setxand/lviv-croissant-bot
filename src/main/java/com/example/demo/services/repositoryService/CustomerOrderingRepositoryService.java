package com.example.demo.services.repositoryService;

import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.entities.peopleRegister.User;

import java.util.List;

public interface CustomerOrderingRepositoryService {
	public List<CustomerOrdering> findAll();

	public CustomerOrdering findOne(Long id);

	public CustomerOrdering findByPhoneNumber(String phoneNumber);

	public CustomerOrdering findTop();

	public void saveAndFlush(CustomerOrdering customerOrdering);

	public void delete(CustomerOrdering customerOrdering);

	public CustomerOrdering findTopByUser(User user);

	public CustomerOrdering findTopByTUser(TUser tUser);
}
