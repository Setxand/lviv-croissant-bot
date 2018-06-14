package com.example.demo.dto.uniRequestModel;

import com.example.demo.entity.lvivCroissants.CroissantsFilling;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CroissantFillingModel {
    private String name;
    private Integer price;

    public CroissantFillingModel(CroissantsFilling croissantsFilling) {
        this.name = croissantsFilling.getName();
        this.price = croissantsFilling.getPrice();
    }
}
