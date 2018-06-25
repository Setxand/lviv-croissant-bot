package com.bots.lvivcroissantbot.repository;

import com.bots.lvivcroissantbot.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportEntityRepository extends JpaRepository<Support, Long> {
    public Support findByUserId(Long userId);

}
