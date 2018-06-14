package com.example.demo.service.uniService.impl;

import com.example.demo.dto.uniRequestModel.CroissantDTO;
import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CroissantsFilling;
import com.example.demo.dto.uniRequestModel.CroissantFillingModel;
import com.example.demo.repository.CroissantEntityRepository;
import com.example.demo.service.uniService.CroissantService;
import com.example.demo.tools.CroissantUtilManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CroissantServiceImpl implements CroissantService {
    @Autowired
    private CroissantEntityRepository croissantEntityRepository;

    @Override
    public List<CroissantDTO> getCroissants() {
        List<CroissantEntity>croissantEntities = croissantEntityRepository.findAll();
        return croissantEntities.stream().map(croissantEntity -> {
            CroissantDTO croissantDTO = new CroissantDTO();
            croissantDTO.setName(croissantEntity.getName());
            croissantDTO.setImageAddress(croissantEntity.getImageUrl());
            croissantDTO.setType(croissantEntity.getType());
            croissantDTO.setPrice(croissantEntity.getPrice());
            croissantDTO.setCroissantsFillings(croissantEntity.getCroissantsFillings().stream().map(fillingEntity->{
                CroissantFillingModel croissantFillingModel= new CroissantFillingModel();
                croissantFillingModel.setName(fillingEntity.getName());
                croissantFillingModel.setPrice(fillingEntity.getPrice());
                return croissantFillingModel;
            }).collect(Collectors.toList()));
            return croissantDTO;
        }).collect(Collectors.toList());

    }

    @Override
    public CroissantDTO createCroissant(CroissantDTO croissantDTO) {
        return null;
    }

//    @Override
//    public CroissantDTO createCroissant(CroissantDTO croissantDTO) {
//        CroissantEntity croissantEntity = new CroissantEntity(croissantDTO);
//
//        for (CroissantFillingModel croissantFillingModel : croissantDTO.getCroissantsFillings())
//            croissantEntity.getCroissantsFillings().add(new CroissantsFilling(croissantFillingModel));
//
//         croissantEntityRepository.saveAndFlush(croissantEntity);
//
//         return croissantDTO;
//
//    }

    @Override
    public CroissantEntity putCroissant(CroissantDTO croissantDTO, Long id) {
        return null;
    }


    //    @Override
//    public CroissantEntity putCroissant(CroissantDTO croissantDTO, Long id) {
//        CroissantEntity croissantEntityEntity = croissantEntityRepository.findOne(id);
//        croissantEntityEntity.setName(croissantDTO.getName());
//        croissantEntityEntity.setImageUrl(croissantDTO.getImageAddress());
//        croissantEntityEntity.setPrice(croissantDTO.getPrice());
//        croissantEntityEntity.setType(croissantDTO.getType());
//
//        return croissantEntityRepository.saveAndFlush(croissantEntityEntity);
//
//
//    }
    @Override
    public Optional<CroissantDTO>findById(Long id) {
        CroissantEntity croissantEntity = croissantEntityRepository.findOne(id);
        Optional<CroissantDTO> croissantDTO = Optional.ofNullable(CroissantUtilManager.CroissantEntityToDTO(croissantEntity));
        return croissantDTO;
    }

}
