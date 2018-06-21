package com.bots.lvivCroissantBot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JsonResponse {
    private String error;
    @JsonProperty("status_code")
    private Integer statusCode;
    private String message;

    public JsonResponse(String error, Integer statusCode, String message) {
        this.error = error;
        this.statusCode = statusCode;
        this.message = message;
    }
}
