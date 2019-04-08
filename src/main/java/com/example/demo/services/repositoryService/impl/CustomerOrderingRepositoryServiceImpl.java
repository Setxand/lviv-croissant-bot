package com.example.demo.services.repositoryService.impl;

import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.entities.peopleRegister.User;
import com.example.demo.repository.CustomerOrderingRepository;
import com.example.demo.services.repositoryService.CustomerOrderingRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerOrderingRepositoryServiceImpl implements CustomerOrderingRepositoryService {

	@Autowired
	private CustomerOrderingRepository customerOrderingRepository;


	@Override
	public List<CustomerOrdering> findAll() {
		return customerOrderingRepository.findAll();
	}

	@Override
	public CustomerOrdering findOne(Long id) {
		return customerOrderingRepository.findOne(id);
	}

	@Override
	public CustomerOrdering findByPhoneNumber(String phoneNumber) {
		return customerOrderingRepository.findByPhoneNumber(phoneNumber);
	}

	@Override
	public CustomerOrdering findTop() {
		return customerOrderingRepository.findTopByOrderByIdDesc();
	}

	@Override
	public void saveAndFlush(CustomerOrdering customerOrdering) {
		customerOrderingRepository.saveAndFlush(customerOrdering);
	}


	@Override
	public void delete(CustomerOrdering customerOrdering) {
		customerOrderingRepository.delete(customerOrdering);
	}

	@Override
	public CustomerOrdering findTopByUser(User user) {
		return customerOrderingRepository.findTopByUserOrderByIdDesc(user);
	}

	@Override
	public CustomerOrdering findTopByTUser(TUser tUser) {
		return customerOrderingRepository.findTopByTUserOrderByIdDesc(tUser);
	}

}
