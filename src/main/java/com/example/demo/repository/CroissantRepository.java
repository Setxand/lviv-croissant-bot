package com.example.demo.repository;

import com.example.demo.entities.lvivCroissants.Croissant;
import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CroissantRepository extends JpaRepository<Croissant,Long>{
    public List<Croissant> getCroissantsByCroissantsFillings(List<CroissantsFilling> croissantsFillings);
    public List<Croissant> getCroissantByCroissantsFillings(CroissantsFilling croissantsFilling);
    public Croissant getCroissantByName(String name);
    public Croissant findTopByOrderByIdDesc();
    public List<Croissant> findAllByTypeOrderByIdDesc(String type);
    public Croissant findTopByCreatorIdOrderByIdDesc(Long creatorId);
}
