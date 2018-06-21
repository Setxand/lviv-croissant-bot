package com.bots.lvivCroissantBot.service.repository;

import com.bots.lvivCroissantBot.entity.lvivCroissants.MenuOfFilling;

import java.util.List;

public interface MenuOfFillingRepositoryService {

    public List<MenuOfFilling> getAll();
    public List<MenuOfFilling> getFillingByPrice(int price);

    public MenuOfFilling findOne(Long id);
    public MenuOfFilling getFillingByName(String name);

    public void saveAndFlush(MenuOfFilling croissantsFilling);
    public void remove(MenuOfFilling croissantsFilling);

}
