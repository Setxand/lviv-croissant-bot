package com.example.demo.models.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TelegramEntity {
    private Integer offset;
    private Integer length;
    private String type;     
}
