package com.bots.lvivcroissantbot.dto.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
public class Element {
    private String title;
    private String subtitle;
    private String image_url;
    private List<Button> buttons = new ArrayList<>();

}
