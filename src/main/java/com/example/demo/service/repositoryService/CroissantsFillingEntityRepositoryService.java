package com.example.demo.service.repositoryService;

import com.example.demo.entity.lvivCroissants.CroissantsFilling;

import java.util.List;

public interface CroissantsFillingEntityRepositoryService {
    public List<CroissantsFilling> getAll();
    public List<CroissantsFilling> getFillingByPrice(int price);

    public CroissantsFilling findOne(Long id);
    public CroissantsFilling getFillingByName(String name);

    public void saveAndFlush(CroissantsFilling croissantsFilling);
    public void remove(CroissantsFilling croissantsFilling);
}
