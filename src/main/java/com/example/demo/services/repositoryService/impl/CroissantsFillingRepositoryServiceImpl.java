package com.example.demo.services.repositoryService.impl;

import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import com.example.demo.repository.CroisantsFillingRepository;
import com.example.demo.services.repositoryService.CroissantsFillingRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CroissantsFillingRepositoryServiceImpl implements CroissantsFillingRepositoryService {
    
    @Autowired
    private CroisantsFillingRepository croisantsFillingRepository;

    @Override
    public List<CroissantsFilling> getAll() {
        return croisantsFillingRepository.findAll();
    }

    @Override
    public List<CroissantsFilling> getFillingByPrice(int price) {
        return croisantsFillingRepository.getFillingByPrice(price);
    }

    @Override
    public CroissantsFilling findOne(Long id) {
        return croisantsFillingRepository.findOne(id);
    }

    @Override
    public CroissantsFilling getFillingByName(String name) {
        return croisantsFillingRepository.getFillingByName(name);
    }

    @Override
    public void saveAndFlush(CroissantsFilling croissantsFilling) {
        croisantsFillingRepository.saveAndFlush(croissantsFilling);

    }




    @Override
    public void remove(CroissantsFilling croissantsFilling) {
        croisantsFillingRepository.delete(croissantsFilling);
    }

}
