package com.example.demo.services.peopleRegisterService.impl;

import com.example.demo.entity.peopleRegister.TUser;
import com.example.demo.constcomponent.telegramEnums.TelegramUserStatus;
import com.example.demo.repository.TelegramUserRepository;
import com.example.demo.services.peopleRegisterService.TelegramUserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramUserRepositoryServiceImpl implements TelegramUserRepositoryService {
	@Autowired
	private TelegramUserRepository telegramUserRepository;

	@Override
	public void delete(TUser tUser) {
		telegramUserRepository.delete(tUser);
	}

	@Override
	public TUser saveAndFlush(TUser tUser) {
		return telegramUserRepository.saveAndFlush(tUser);
	}

	@Override
	public TUser findByChatId(Integer chatId) {
		return telegramUserRepository.findByChatId(chatId);
	}

	@Override
	public void changeStatus(TUser user, TelegramUserStatus status) {
		user.setStatus(status);
		telegramUserRepository.saveAndFlush(user);
	}

	@Override
	public TUser findByUserName(String userName) {
		return telegramUserRepository.findByUserName(userName);
	}

	@Override
	public List<String> findTopUserNames() {
		return telegramUserRepository.findTopw();
	}
}
