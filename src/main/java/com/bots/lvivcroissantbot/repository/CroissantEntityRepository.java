package com.bots.lvivcroissantbot.repository;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CroissantEntityRepository extends JpaRepository<CroissantEntity, Long> {
    public CroissantEntity getCroissantByName(String name);

    public CroissantEntity findTopByOrderByIdDesc();

    public List<CroissantEntity> findAllByTypeOrderByIdDesc(String type);

    public CroissantEntity findTopByCreatorIdOrderByIdDesc(Long creatorId);
}
