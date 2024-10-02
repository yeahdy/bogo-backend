package com.boardgo.domain.chatting.service.response;

import java.time.LocalDateTime;

public record ChattingListResponse(
	Long chatRoomId,
	String thumbnail,
	String meetingTitle,
	String lastMessage,
	LocalDateTime lastSendDatetime
) { }
