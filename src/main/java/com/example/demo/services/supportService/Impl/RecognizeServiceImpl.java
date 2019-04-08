package com.example.demo.services.supportService.Impl;

import com.example.demo.entities.peopleRegister.User;
import com.example.demo.services.peopleRegisterService.UserRepositoryService;
import com.example.demo.services.supportService.RecognizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

@Service
public class RecognizeServiceImpl implements RecognizeService {
	@Autowired
	private UserRepositoryService userRepositoryService;

	@Override
	public String recognize(String text, Long userId) {
		User user = userRepositoryService.findOnebyRId(userId);
		return ResourceBundle.getBundle("dictionary", user.getLocale()).getString(text);
	}
}
