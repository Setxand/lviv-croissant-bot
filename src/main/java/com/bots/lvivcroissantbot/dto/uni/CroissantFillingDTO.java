package com.bots.lvivcroissantbot.dto.uni;

import com.bots.lvivcroissantbot.entity.lvivcroissants.CroissantsFilling;
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
public class CroissantFillingDTO {
    @NotNull
    @Max(32)
    private String name;
    @NotNull
    private Integer price;

    public CroissantFillingDTO(CroissantsFilling croissantsFilling) {
        this.name = croissantsFilling.getName();
        this.price = croissantsFilling.getPrice();
    }
}
