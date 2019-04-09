package com.example.demo.repository;

import com.example.demo.entity.lvivCroissants.MenuOfFilling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOfFillingRepository extends JpaRepository<MenuOfFilling, Long> {
	public List<MenuOfFilling> getFillingByPrice(int price);

	public MenuOfFilling getFillingByName(String name);

}
