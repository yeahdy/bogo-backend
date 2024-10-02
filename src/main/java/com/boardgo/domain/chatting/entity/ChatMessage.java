package com.boardgo.domain.chatting.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document("chat_message")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
	@Id
	private String id;
	private Long roomId;
	private Long userId;
	private String content;
	private LocalDateTime sendDatetime;

	@Builder
	private ChatMessage(String id, Long roomId, Long userId, String content, LocalDateTime sendDatetime) {
		this.id = id;
		this.roomId = roomId;
		this.userId = userId;
		this.content = content;
		this.sendDatetime = sendDatetime;
	}
}
