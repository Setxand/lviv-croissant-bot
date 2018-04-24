package com.example.demo.models.messanger.broadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Value {
    private Long value;
    @JsonProperty("end_time")
    private String endTime;

    @Override
    public String toString() {
        return "Value{" +
                "value=" + value +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
