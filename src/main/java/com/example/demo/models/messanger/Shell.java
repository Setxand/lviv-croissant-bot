package com.example.demo.models.messanger;

import com.example.demo.enums.messengerEnums.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class Shell {
    @JsonProperty("whitelisted_domains")
    private List<String>whiteListedDomains ;
    private Objects object;
    @JsonProperty("callback_url")
    private String callbackUrl;
    private String[] fields;
    @JsonProperty("verify_token")
    private String verToken;
    @JsonProperty("include_values")
    private Boolean includeValues;
    @JsonProperty("access_token")
    private String accessToken;

}
