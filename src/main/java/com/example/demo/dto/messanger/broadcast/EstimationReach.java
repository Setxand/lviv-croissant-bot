package com.example.demo.dto.messanger.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class EstimationReach {
    @JsonProperty("reach_estimation")
    private String reachEstimation;
    private String id;

    @Override
    public String toString() {
        return "\n\nEstimationReach{" +
                "reachEstimation='" + reachEstimation + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
