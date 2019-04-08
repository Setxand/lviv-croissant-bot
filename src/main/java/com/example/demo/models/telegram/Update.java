package com.example.demo.models.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Update {
	@JsonProperty("update_id")
	private Integer updateId;
	@JsonProperty("callback_query")
	private CallBackQuery callBackQuery;
	private Message message;
}
