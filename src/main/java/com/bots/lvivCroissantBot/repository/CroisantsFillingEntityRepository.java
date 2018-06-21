package com.bots.lvivCroissantBot.repository;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantsFilling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CroisantsFillingEntityRepository extends JpaRepository<CroissantsFilling,Long> {
    public List<CroissantsFilling> getFillingByPrice(int price);
    public CroissantsFilling getFillingByName(String name);

}
