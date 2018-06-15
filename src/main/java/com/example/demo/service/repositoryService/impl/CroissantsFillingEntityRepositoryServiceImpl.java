package com.example.demo.service.repositoryService.impl;

import com.example.demo.entity.lvivCroissants.CroissantsFilling;
import com.example.demo.repository.CroisantsFillingEntityRepository;
import com.example.demo.service.repositoryService.CroissantsFillingEntityRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CroissantsFillingEntityRepositoryServiceImpl implements CroissantsFillingEntityRepositoryService {
    
    @Autowired
    private CroisantsFillingEntityRepository croisantsFillingEntityRepository;

    @Override
    public List<CroissantsFilling> getAll() {
        return croisantsFillingEntityRepository.findAll();
    }

    @Override
    public List<CroissantsFilling> getFillingByPrice(int price) {
        return croisantsFillingEntityRepository.getFillingByPrice(price);
    }

    @Override
    public CroissantsFilling findOne(Long id) {
        return croisantsFillingEntityRepository.findOne(id);
    }

    @Override
    public CroissantsFilling getFillingByName(String name) {
        return croisantsFillingEntityRepository.getFillingByName(name);
    }

    @Override
    public void saveAndFlush(CroissantsFilling croissantsFilling) {
        croisantsFillingEntityRepository.saveAndFlush(croissantsFilling);

    }




    @Override
    public void remove(CroissantsFilling croissantsFilling) {
        croisantsFillingEntityRepository.delete(croissantsFilling);
    }

}
