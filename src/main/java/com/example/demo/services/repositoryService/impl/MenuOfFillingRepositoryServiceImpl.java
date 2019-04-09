package com.example.demo.services.repositoryService.impl;

import com.example.demo.entity.lvivCroissants.MenuOfFilling;
import com.example.demo.repository.MenuOfFillingRepository;
import com.example.demo.services.repositoryService.MenuOfFillingRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuOfFillingRepositoryServiceImpl implements MenuOfFillingRepositoryService {
	@Autowired
	private MenuOfFillingRepository menuOfFillingRepository;

	@Override
	public List<MenuOfFilling> getAll() {
		return menuOfFillingRepository.findAll();
	}

	@Override
	public List<MenuOfFilling> getFillingByPrice(int price) {
		return menuOfFillingRepository.getFillingByPrice(price);
	}

	@Override
	public MenuOfFilling findOne(Long id) {
		return menuOfFillingRepository.findOne(id);
	}

	@Override
	public MenuOfFilling getFillingByName(String name) {
		return menuOfFillingRepository.getFillingByName(name);
	}

	@Override
	public void saveAndFlush(MenuOfFilling croissantsFilling) {
		menuOfFillingRepository.saveAndFlush(croissantsFilling);
	}

	@Override
	public void remove(MenuOfFilling croissantsFilling) {
		menuOfFillingRepository.delete(croissantsFilling);
	}


}
