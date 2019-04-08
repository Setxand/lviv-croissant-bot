package com.example.demo.models.messanger;

import com.example.demo.models.messanger.broadcast.DynamicText;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Message {
	private String mid;
	private Integer seq;
	private String text;
	@JsonProperty("quick_reply")
	private QuickReply quickReply;
	@JsonProperty("quick_replies")
	private List<QuickReply> quickReplies;
	private Attachment attachment;

	@JsonProperty("dynamic_text")
	private DynamicText dynamicText;

	public Message(String text) {
		this.text = text;
	}

	public Message() {
	}

}
