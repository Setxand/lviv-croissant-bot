package com.example.demo.repository;

import com.example.demo.entities.lvivCroissants.CustomerOrdering;
import com.example.demo.entities.peopleRegister.TUser;
import com.example.demo.entities.peopleRegister.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderingRepository extends JpaRepository<CustomerOrdering, Long> {
	public CustomerOrdering findByPhoneNumber(String phoneNumber);

	public CustomerOrdering findTopByOrderByIdDesc();

	public CustomerOrdering findTopByUserOrderByIdDesc(User user);

	public CustomerOrdering findTopByTUserOrderByIdDesc(TUser tUser);


}
