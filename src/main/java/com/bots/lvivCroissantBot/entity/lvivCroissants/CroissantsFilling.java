package com.bots.lvivCroissantBot.entity.lvivCroissants;

import com.bots.lvivCroissantBot.dto.uniRequestModel.CroissantFillingModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CroissantsFilling {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String name;
    private int price;

    @ManyToOne
    @JoinColumn(name = "croissant_id")
    private CroissantEntity croissantEntity;

    public CroissantsFilling() {
    }
    public CroissantsFilling(MenuOfFilling other){
        this.name = other.getName();
        this.price = other.getPrice();
    }

    public CroissantsFilling(String name, int price) {
        this.name = name;
        this.price = price;
    }
    public CroissantsFilling(CroissantFillingModel croissantFillingModel){
        this.name = croissantFillingModel.getName();
        this.price = croissantFillingModel.getPrice();
    }


}
