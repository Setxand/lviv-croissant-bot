package com.example.demo.repository;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.constcomponent.messengerEnums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
	public User findTopByOrderByIdDesc();

	public User findByRecipientId(Long recipientId);

	public List<User> findAllByRole(Roles roles);
}
