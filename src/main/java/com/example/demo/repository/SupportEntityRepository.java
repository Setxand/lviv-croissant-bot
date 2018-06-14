package com.example.demo.repository;

import com.example.demo.entity.SupportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportEntityRepository extends JpaRepository<SupportEntity,Long>{
    public SupportEntity findByUserId(Long userId);

}
