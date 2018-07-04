package com.bots.lvivcroissantbot.repository;

import com.bots.lvivcroissantbot.entity.lvivcroissants.MenuOfFilling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOfFillingRepository extends JpaRepository<MenuOfFilling, Long> {
    public List<MenuOfFilling> getFillingByPrice(int price);

    public MenuOfFilling getFillingByName(String name);

}