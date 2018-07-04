package com.bots.lvivcroissantbot.service.uni;

import com.bots.lvivcroissantbot.dto.uni.CroissantDTO;
import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import com.bots.lvivcroissantbot.exception.ElementNoFoundException;
import com.bots.lvivcroissantbot.repository.CroissantEntityRepository;
import com.bots.lvivcroissantbot.tools.CroissantUtilManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CroissantService {

    private final CroissantEntityRepository croissantEntityRepository;


    public CroissantService(CroissantEntityRepository croissantEntityRepository) {
        this.croissantEntityRepository = croissantEntityRepository;
    }

    public List<CroissantDTO> getCroissants() {
        List<CroissantEntity> croissantEntities = croissantEntityRepository.findAll();
        return croissantEntities.stream().map(CroissantUtilManager::croissantEntityToDTO).collect(Collectors.toList());

    }


    public CroissantDTO createCroissant(CroissantDTO croissantDTO) {
        CroissantEntity croissantEntity = CroissantUtilManager.croissantDTOToEntity(croissantDTO);
        croissantEntityRepository.saveAndFlush(croissantEntity);
        return croissantDTO;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void putCroissant(CroissantDTO croissantDTO, Long id) {
        CroissantEntity croissantEntity = croissantEntityRepository.findById(id).orElseThrow(ElementNoFoundException::new);
        croissantEntity.setName(croissantDTO.getName());
        croissantEntity.setImageUrl(croissantDTO.getImageAddress());
        croissantEntity.setPrice(croissantDTO.getPrice());
        croissantEntity.setType(croissantDTO.getType());
        croissantEntity.setCroissantsFillings(CroissantUtilManager.fillingDTOToEntity(croissantDTO.getCroissantsFillings()));
//        croissantEntityRepository.saveAndFlush(croissantEntity);
    }

    public Optional<CroissantDTO> findByIdAndReturnDTO(Long id) {
        CroissantEntity croissantEntity = croissantEntityRepository.findById(id).orElseThrow(ElementNoFoundException::new);
        return Optional.ofNullable(croissantEntity).map(CroissantUtilManager::croissantEntityToDTO);
    }

}
