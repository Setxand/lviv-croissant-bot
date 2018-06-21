package com.bots.lvivCroissantBot.service.repositoryService.impl;

import com.bots.lvivCroissantBot.dto.uniRequestModel.CroissantDTO;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantsFilling;
import com.bots.lvivCroissantBot.dto.uniRequestModel.CroissantFillingModel;
import com.bots.lvivCroissantBot.repository.CroissantEntityRepository;
import com.bots.lvivCroissantBot.service.repositoryService.CroissantRepositoryService;
import com.bots.lvivCroissantBot.tools.CroissantUtilManager;
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
            CroissantDTO croissantDTO;

            croissantDTO = CroissantUtilManager.croissantEntityToDTO(croissantEntity);
            for(CroissantsFilling croissantsFilling: croissantEntity.getCroissantsFillings()){
                croissantDTO.getCroissantsFillings().add(new CroissantFillingModel(croissantsFilling));
            }
            croissantDTOS.add(croissantDTO);

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
