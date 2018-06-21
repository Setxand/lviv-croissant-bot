package com.bots.lvivCroissantBot.dto.uni;

import com.bots.lvivCroissantBot.entity.lvivCroissants.CroissantsFilling;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CroissantFillingModel {
    @NotNull
    @Max(32)
    private String name;
    @NotNull
    private Integer price;

    public CroissantFillingModel(CroissantsFilling croissantsFilling) {
        this.name = croissantsFilling.getName();
        this.price = croissantsFilling.getPrice();
    }
}
