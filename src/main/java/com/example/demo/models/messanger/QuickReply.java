package com.example.demo.models.messanger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuickReply {
	@JsonProperty("content_type")
	private String contentType;
	private String title;
	private String payload;
	@JsonProperty("image_url")
	private String imageUrl;


	public QuickReply(String contentType) {
		this.contentType = contentType;
	}

	public QuickReply(String contentType, String title, String payload) {
		this.contentType = contentType;
		this.title = title;
		this.payload = payload;
	}

	public QuickReply() {
	}

}
