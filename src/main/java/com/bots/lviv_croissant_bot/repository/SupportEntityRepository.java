package com.bots.lviv_croissant_bot.repository;

import com.bots.lviv_croissant_bot.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportEntityRepository extends JpaRepository<Support,Long>{
    public Support findByUserId(Long userId);

}
