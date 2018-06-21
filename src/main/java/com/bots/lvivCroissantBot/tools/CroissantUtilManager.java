package com.bots.lvivCroissantBot.tools;

import com.bots.lvivCroissantBot.dto.uni.CroissantDTO;
import com.bots.lvivCroissantBot.dto.uni.CroissantFillingModel;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantEntity;
import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantsFilling;

import java.util.List;
import java.util.stream.Collectors;

public class CroissantUtilManager {
    public static CroissantDTO croissantEntityToDTO(CroissantEntity croissantEntity){
        CroissantDTO croissantDTO = new CroissantDTO();
        croissantDTO.setName(croissantEntity.getName());
        croissantDTO.setImageAddress(croissantEntity.getImageUrl());
        croissantDTO.setPrice(croissantEntity.getPrice());
        croissantDTO.setType(croissantEntity.getType());
        croissantDTO.setCroissantsFillings(fillingsEntityToDTO(croissantEntity.getCroissantsFillings()));

        return croissantDTO;
    }


    private static List<CroissantFillingModel> fillingsEntityToDTO(List<CroissantsFilling> croissantsFillings){

        return croissantsFillings.stream().map(filling ->{
            CroissantFillingModel croissantFillingModel = new CroissantFillingModel();
            croissantFillingModel.setName(filling.getName());
            croissantFillingModel.setPrice(filling.getPrice());
            return croissantFillingModel;
        }).collect(Collectors.toList());
    }

    public static CroissantEntity croissantDTOToEntity(CroissantDTO croissantDTO) {
        CroissantEntity croissantEntity = new CroissantEntity();
        croissantEntity.setName(croissantDTO.getName());
        croissantEntity.setImageUrl(croissantDTO.getImageAddress());
        croissantEntity.setPrice(croissantDTO.getPrice());
        croissantEntity.setType(croissantDTO.getType());
        List<CroissantsFilling> croissantsFillings = fillingDTOToEntity(croissantDTO.getCroissantsFillings());
        for (CroissantsFilling filling : croissantsFillings) {
            croissantEntity.addSingleFilling(filling);
        }
        return croissantEntity;
    }

    public static List<CroissantsFilling> fillingDTOToEntity(List<CroissantFillingModel> croissantsFillings) {
        return croissantsFillings.stream().map(filling -> {
            CroissantsFilling croissantsFilling = new CroissantsFilling();
            croissantsFilling.setName(filling.getName());
            croissantsFilling.setPrice(filling.getPrice());
            return croissantsFilling;
        }).collect(Collectors.toList());
    }


}



