package com.example.demo.services.supportService.Impl;

import com.example.demo.entity.peopleRegister.User;
import com.example.demo.model.messanger.Messaging;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VerifyServiceImpl implements VerifyService {
	@Autowired
	private UserRepositoryService userRepositoryService;

	@Value("${app.verify.token}")
	private String VER_TOK;

	@Override
	public boolean verify(String verifyToken) {
		if (verifyToken.equals(VER_TOK))
			return true;
		else
			return false;
	}


	@Override
	public boolean isCustomer(Messaging messaging) {
		User user = userRepositoryService.findOnebyRId(messaging.getSender().getId());
		if (user.getEmail() == null || user.getName() == null || user.getPhoneNumber() == null || user.getAddress() == null)
			return false;


		return true;
	}
}
