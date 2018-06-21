package com.bots.lvivCroissantBot.service.repositoryService;

import com.bots.lvivCroissantBot.dto.uniRequestModel.CroissantDTO;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;

import java.util.List;

public interface CroissantRepositoryService {
    public List<CroissantDTO> findAll();

    public List<CroissantEntity> findAllByType(String type);
    public CroissantEntity findLastRecord();

    public CroissantEntity findOne(Long id);
    public CroissantEntity getCroissantByName(String name);

    public CroissantEntity saveAndFlush(CroissantEntity croissantEntity);
    public void remove(CroissantEntity croissantEntity);
    public CroissantEntity findLastByCreatorId(Long creatorId);
}
