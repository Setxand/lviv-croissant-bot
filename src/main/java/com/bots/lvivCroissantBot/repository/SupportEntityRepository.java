package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportEntityRepository extends JpaRepository<Support,Long>{
    public Support findByUserId(Long userId);

}
