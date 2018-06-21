package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.SupportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportEntityRepository extends JpaRepository<SupportEntity,Long>{
    public SupportEntity findByUserId(Long userId);

}
