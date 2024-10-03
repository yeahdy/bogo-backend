package com.boardgo.domain.chatting.service.response;

import com.boardgo.domain.chatting.entity.ChatMessage;
import org.springframework.data.mongodb.core.mapping.Field;

public record LatestMessageResponse(
        @Field("_id") Long roomId, @Field("latestMessage") ChatMessage latestMessage) {}
