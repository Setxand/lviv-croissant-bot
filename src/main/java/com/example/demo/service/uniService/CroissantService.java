package com.example.demo.service.uniService;

import com.example.demo.dto.uniRequestModel.CroissantDTO;
import com.example.demo.entity.lvivCroissants.CroissantEntity;

import java.util.List;
import java.util.Optional;

public interface CroissantService {
    List<CroissantDTO>getCroissants();
    CroissantDTO createCroissant(CroissantDTO croissantDTO);
    CroissantEntity putCroissant(CroissantDTO croissantDTO, Long id);
    Optional<CroissantDTO >findById(Long id);
}
