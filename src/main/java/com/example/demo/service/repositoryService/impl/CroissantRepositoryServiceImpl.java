package com.example.demo.service.repositoryService.impl;

import com.example.demo.dto.uniRequestModel.CroissantDTO;
import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CroissantsFilling;
import com.example.demo.dto.uniRequestModel.CroissantFillingModel;
import com.example.demo.repository.CroissantEntityRepository;
import com.example.demo.service.repositoryService.CroissantRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class CroissantRepositoryServiceImpl implements CroissantRepositoryService {
    @Autowired
    private CroissantEntityRepository croissantEntityRepository;


    @Override
    public List<CroissantDTO> findAll() {
        List<CroissantDTO> croissantDTOS = new ArrayList<>();
        for(CroissantEntity croissantEntity : croissantEntityRepository.findAll()){
            CroissantDTO croissantDTODTO;
            croissantDTODTO = new CroissantDTO(croissantEntity);
            for(CroissantsFilling croissantsFilling: croissantEntity.getCroissantsFillings()){
                croissantDTODTO.getCroissantsFillings().add(new CroissantFillingModel(croissantsFilling));
            }
            croissantDTOS.add(croissantDTODTO);

        }
        return croissantDTOS;
    }




    @Override
    public List<CroissantEntity> findAllByType(String type) {
        return croissantEntityRepository.findAllByTypeOrderByIdDesc(type);
    }

    @Override
    public CroissantEntity findLastRecord() {
        return croissantEntityRepository.findTopByOrderByIdDesc();
    }


    @Override
    public CroissantEntity findOne(Long id) {
        return croissantEntityRepository.findOne(id);
    }

    @Override
    public CroissantEntity getCroissantByName(String name) {
        return this.croissantEntityRepository.getCroissantByName(name);
    }

    @Override
    public CroissantEntity saveAndFlush(CroissantEntity croissantEntity) {
        return croissantEntityRepository.saveAndFlush(croissantEntity);
    }

    @Override
    public void remove(CroissantEntity croissantEntity) {
        croissantEntityRepository.delete(croissantEntity);
    }

    @Override
    public CroissantEntity findLastByCreatorId(Long creatorId) {
        return croissantEntityRepository.findTopByCreatorIdOrderByIdDesc(creatorId);
    }


}
