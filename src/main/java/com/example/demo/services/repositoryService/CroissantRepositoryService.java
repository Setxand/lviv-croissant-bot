package com.example.demo.services.repositoryService;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CroissantsFilling;

import java.util.List;

public interface CroissantRepositoryService {
	public List<Croissant> findAll();

	public List<Croissant> getCroissantsByFillings(List<CroissantsFilling> croissantsFillings);

	public List<Croissant> getCroissantByFillings(CroissantsFilling croissantsFilling);

	public List<Croissant> findAllByType(String type);

	public Croissant findLastRecord();

	public Croissant findOne(Long id);

	public Croissant getCroissantByName(String name);

	public Croissant saveAndFlush(Croissant croissant);

	public void remove(Croissant croissant);

	public Croissant findLastByCreatorId(Long creatorId);
}
