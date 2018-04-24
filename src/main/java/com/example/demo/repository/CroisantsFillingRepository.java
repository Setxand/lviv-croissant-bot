package com.example.demo.repository;

import com.example.demo.entities.lvivCroissants.CroissantsFilling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface   CroisantsFillingRepository extends JpaRepository<CroissantsFilling,Long> {
    public List<CroissantsFilling> getFillingByPrice(int price);
    public CroissantsFilling getFillingByName(String name);

}
