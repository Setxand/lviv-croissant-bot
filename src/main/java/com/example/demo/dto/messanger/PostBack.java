package com.example.demo.dto.messanger;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PostBack {
   private String title;
   private String payload;

    public PostBack() {
    }

    public PostBack(String payload) {
        this.payload = payload;
    }
}
