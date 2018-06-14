package com.example.demo.tools;

import com.example.demo.dto.uniRequestModel.CroissantDTO;
import com.example.demo.dto.uniRequestModel.CroissantFillingModel;
import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.example.demo.entity.lvivCroissants.CroissantsFilling;

import java.util.List;
import java.util.stream.Collectors;

public class CroissantUtilManager {
    public static CroissantDTO CroissantEntityToDTO(CroissantEntity croissantEntity){
        CroissantDTO croissantDTO = new CroissantDTO();
        croissantDTO.setName(croissantEntity.getName().get().toString());
        croissantDTO.setImageAddress(croissantEntity.getImageUrl().get().toString());
        croissantDTO.setPrice(Integer.parseInt(croissantEntity.getPrice().get().toString()));
        croissantDTO.setType(croissantEntity.getType().get().toString());
//        croissantDTO.setCroissantsFillings(fillingsEntityToDTO(croissantEntity.getCroissantsFillings()));
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

    public static CroissantDTO croissantDTOToEntity(CroissantDTO croissantDTO){
        CroissantEntity croissantEntity = new CroissantEntity();
        croissantEntity.setName(croissantDTO.getName().get());
        croissantEntity.setImageUrl(croissantDTO.getImageAddress().get());
        croissantEntity.setPrice(croissantDTO.getPrice().get());
        croissantEntity.setType(croissantDTO.getType().get());
        croissantEntity.setCroissantsFillings(fillingsDTOToEntity(croissantDTO.getCroissantsFillings()));
        return croissantDTO;
    }


    private static List<CroissantsFilling> fillingsDTOToEntity(List<CroissantFillingModel> croissantFillingModels){

        return croissantFillingModels.stream().map(filling ->{
            CroissantsFilling croissantFilling = new CroissantsFilling();
            croissantFilling.setName(filling.getName());
            croissantFilling.setPrice(filling.getPrice());
            return croissantFilling;

        }).collect(Collectors.toList());
    }

}
