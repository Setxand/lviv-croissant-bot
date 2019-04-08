package com.example.demo.services.repositoryService;

import com.example.demo.entities.lvivCroissants.CroissantsFilling;

import java.util.List;

public interface CroissantsFillingRepositoryService {
	public List<CroissantsFilling> getAll();

	public List<CroissantsFilling> getFillingByPrice(int price);

	public CroissantsFilling findOne(Long id);

	public CroissantsFilling getFillingByName(String name);

	public void saveAndFlush(CroissantsFilling croissantsFilling);

	public void remove(CroissantsFilling croissantsFilling);
}
