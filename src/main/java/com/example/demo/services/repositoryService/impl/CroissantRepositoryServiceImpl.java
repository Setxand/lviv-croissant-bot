package com.example.demo.services.repositoryService.impl;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import com.example.demo.repository.CroissantRepository;
import com.example.demo.services.repositoryService.CroissantRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CroissantRepositoryServiceImpl implements CroissantRepositoryService {
    @Autowired
    private CroissantRepository croissantDao;


    @Override
    public List<Croissant> findAll() {
        return croissantDao.findAll();
    }

    @Override
    public List<Croissant> getCroissantsByFillings(List<CroissantsFilling> croissantsFillings) {
        return croissantDao.getCroissantsByCroissantsFillings(croissantsFillings);
    }



    @Override
    public List<Croissant> getCroissantByFillings(CroissantsFilling croissantsFilling) {
        return croissantDao.getCroissantByCroissantsFillings(croissantsFilling);
    }

    @Override
    public List<Croissant> findAllByType(String type) {
        return croissantDao.findAllByTypeOrderByIdDesc(type);
    }

    @Override
    public Croissant findLastRecord() {
        return croissantDao.findTopByOrderByIdDesc();
    }


    @Override
    public Croissant findOne(Long id) {
        return croissantDao.findOne(id);
    }

    @Override
    public Croissant getCroissantByName(String name) {
        return this.croissantDao.getCroissantByName(name);
    }

    @Override
    public Croissant saveAndFlush(Croissant croissant) {
        return croissantDao.saveAndFlush(croissant);
    }

    @Override
    public void remove(Croissant croissant) {
        croissantDao.delete(croissant);
    }

    @Override
    public Croissant findLastByCreatorId(Long creatorId) {
        return croissantDao.findTopByCreatorIdOrderByIdDesc(creatorId);
    }


}
