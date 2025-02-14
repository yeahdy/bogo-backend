package com.boardgo.domain.mapper;

import com.boardgo.domain.chatting.entity.ChatMessage;
import com.boardgo.domain.chatting.repository.projection.LatestMessageProjection;
import com.boardgo.domain.chatting.service.response.ChatMessageResponse;
import com.boardgo.domain.chatting.service.response.ChattingListResponse;
import com.boardgo.domain.chatting.service.response.LatestMessageResponse;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChatMapper {
    ChatMapper INSTANCE = Mappers.getMapper(ChatMapper.class);

    List<ChatMessageResponse> toChatMessageResponseList(List<ChatMessage> chatMessageList);

    List<LatestMessageResponse> toLatestMessageResponse(
            List<LatestMessageProjection> latestMessageProjection);

    default List<ChattingListResponse> toChattingListResponseList(
            List<MeetingEntity> meetingEntityList,
            Map<Long, ChatMessage> chatMessageMap,
            Map<Long, Long> chatRoomMap) {
        return meetingEntityList.stream()
                .map(
                        item ->
                                toChattingListResponse(
                                        item,
                                        chatMessageMap.getOrDefault(
                                                chatRoomMap.get(item.getId()), null),
                                        chatRoomMap.get(item.getId())))
                .sorted(
                        Comparator.comparing(
                                ChattingListResponse::lastSendDatetime,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    default ChattingListResponse toChattingListResponse(
            MeetingEntity meeting, ChatMessage chatMessage, Long chatRoomId) {
        return new ChattingListResponse(
                chatRoomId,
                meeting.getId(),
                meeting.getThumbnail(),
                meeting.getTitle(),
                Objects.isNull(chatMessage) ? null : chatMessage.getContent(),
                Objects.isNull(chatMessage) ? null : chatMessage.getSendDatetime());
    }
}
