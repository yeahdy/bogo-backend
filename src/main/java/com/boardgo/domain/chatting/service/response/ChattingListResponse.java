package com.boardgo.domain.chatting.service.response;

import java.time.LocalDateTime;

public record ChattingListResponse(
        Long chatRoomId,
		Long meetingId,
        String thumbnail,
        String meetingTitle,
        String lastMessage,
        LocalDateTime lastSendDatetime) {}
