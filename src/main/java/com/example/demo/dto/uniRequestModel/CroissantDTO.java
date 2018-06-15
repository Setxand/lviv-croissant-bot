package com.example.demo.dto.uniRequestModel;

import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
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



}
