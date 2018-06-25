package com.bots.lvivcroissantbot.dto.uni;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CroissantDTO {
    private Long id;
    @NotNull
    @Size(min = 8,max = 32)
    private String name;
    @NotNull
    private String imageAddress;
    @NotNull
    private Integer price;
    @NotNull
    private String type;
    @Valid
    private List<CroissantFillingModel> croissantsFillings = new ArrayList<>();

    @Override
    public String toString() {
        return "\n\n"+name+".\n"+"" +
                "Вміст: \n" +
                croissantsFillings;
    }
}
