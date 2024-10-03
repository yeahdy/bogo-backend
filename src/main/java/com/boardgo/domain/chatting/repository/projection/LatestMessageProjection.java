package com.boardgo.domain.chatting.repository.projection;

import com.boardgo.domain.chatting.entity.ChatMessage;
import org.springframework.data.mongodb.core.mapping.Field;

public record LatestMessageProjection(
        @Field("_id") Long roomId, @Field("latestMessage") ChatMessage latestMessage) {}
