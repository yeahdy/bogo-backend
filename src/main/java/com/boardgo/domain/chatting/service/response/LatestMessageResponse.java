package com.boardgo.domain.chatting.service.response;

import org.springframework.data.mongodb.core.mapping.Field;

import com.boardgo.domain.chatting.entity.ChatMessage;

public record LatestMessageResponse(
	@Field("_id")
	 Long roomId,
	@Field("latestMessage")
	 ChatMessage latestMessage
) {
}
