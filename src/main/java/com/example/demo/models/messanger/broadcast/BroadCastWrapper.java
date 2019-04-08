package com.example.demo.models.messanger.broadcast;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BroadCastWrapper<T> {
	T t;

	public BroadCastWrapper(T t) {
		this.t = t;
	}
}
