package com.boardgo.domain.chatting.repository.projection;

import org.springframework.data.mongodb.core.mapping.Field;

import com.boardgo.domain.chatting.entity.ChatMessage;

public record LatestMessageProjection(
	@Field("_id")
	 Long roomId,
	@Field("latestMessage")
	 ChatMessage latestMessage
) {
}
