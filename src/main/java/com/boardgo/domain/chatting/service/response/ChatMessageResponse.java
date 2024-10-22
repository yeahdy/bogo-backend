package com.boardgo.domain.chatting.service.response;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long roomId, Long userId, String content, LocalDateTime sendDatetime) {}
