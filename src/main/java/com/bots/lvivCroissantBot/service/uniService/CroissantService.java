package com.bots.lvivCroissantBot.service.uniService;

import com.bots.lvivCroissantBot.dto.uniRequestModel.CroissantDTO;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.exceptions.ElementNoFoundException;
import com.bots.lvivCroissantBot.repository.CroissantEntityRepository;
import com.bots.lvivCroissantBot.tools.CroissantUtilManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CroissantService {
    @Autowired
    private CroissantEntityRepository croissantEntityRepository;

    public List<CroissantDTO> getCroissants() {
        List<CroissantEntity> croissantEntities = croissantEntityRepository.findAll();
        return croissantEntities.stream().map(CroissantUtilManager::croissantEntityToDTO).collect(Collectors.toList());

    }


    public CroissantDTO createCroissant(CroissantDTO croissantDTO) {
        CroissantEntity croissantEntity = CroissantUtilManager.croissantDTOToEntity(croissantDTO);
        croissantEntityRepository.saveAndFlush(croissantEntity);
        return croissantDTO;
    }


    public void putCroissant(CroissantDTO croissantDTO, Long id) {
        CroissantEntity croissantEntity = croissantEntityRepository.findOne(id);
        if (croissantEntity == null) {
            throw new ElementNoFoundException();
        }
        croissantEntity.setName(croissantDTO.getName());
        croissantEntity.setImageUrl(croissantDTO.getImageAddress());
        croissantEntity.setPrice(croissantDTO.getPrice());
        croissantEntity.setType(croissantDTO.getType());
        croissantEntity.setCroissantsFillings(CroissantUtilManager.fillingDTOToEntity(croissantDTO.getCroissantsFillings()));
        croissantEntityRepository.saveAndFlush(croissantEntity);
    }

    public Optional<CroissantDTO> findById(Long id) {
        CroissantEntity croissantEntity = croissantEntityRepository.findOne(id);
        return Optional.ofNullable(croissantEntity).map(CroissantUtilManager::croissantEntityToDTO);
    }

}
