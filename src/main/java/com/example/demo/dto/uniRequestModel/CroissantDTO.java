package com.example.demo.dto.uniRequestModel;

import com.example.demo.entity.lvivCroissants.CroissantEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CroissantDTO {
    private Optional<Long> id;
    private Optional<String >name;
    private Optional<String>imageAddress;
    private Optional<Integer>price;
    private Optional<String>type;
    private List<CroissantFillingModel> croissantsFillings = new ArrayList<>();


    public Long getId() {
        return id.get();
    }

    public void setId(Long id) {
        this.id = Optional.ofNullable(id);
    }

    public Optional<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Optional.ofNullable(name);
    }

    public Optional<String> getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = Optional.ofNullable(imageAddress);
    }

    public Optional<Integer>getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = Optional.ofNullable(price);
    }

    public Optional<String> getType() {
        return type;
    }

    public void setType(String type) {
        this.type = Optional.ofNullable(type);
    }

    public List<CroissantFillingModel> getCroissantsFillings() {
        return croissantsFillings;
    }

    public void setCroissantsFillings(List<CroissantFillingModel> croissantsFillings) {
        this.croissantsFillings = croissantsFillings;
    }
}
