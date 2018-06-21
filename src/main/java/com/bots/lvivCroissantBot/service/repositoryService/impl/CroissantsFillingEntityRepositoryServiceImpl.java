package com.bots.lvivCroissantBot.service.repositoryService.impl;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantsFilling;
import com.bots.lvivCroissantBot.exception.ElementNoFoundException;
import com.bots.lvivCroissantBot.repository.CroisantsFillingEntityRepository;
import com.bots.lvivCroissantBot.service.repositoryService.CroissantsFillingEntityRepositoryService;
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
        return croisantsFillingEntityRepository.findById(id).orElseThrow(ElementNoFoundException::new);
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
