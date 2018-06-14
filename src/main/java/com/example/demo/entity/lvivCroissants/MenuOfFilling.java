package com.example.demo.entity.lvivCroissants;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class MenuOfFilling {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int price;

    public MenuOfFilling(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public MenuOfFilling() {
    }



    @Override
    public String toString() {
        return id+". "+name;
    }
}
