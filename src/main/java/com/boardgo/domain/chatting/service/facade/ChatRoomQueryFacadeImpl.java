package com.boardgo.domain.chatting.service.facade;

import com.boardgo.domain.chatting.entity.ChatMessage;
import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import com.boardgo.domain.chatting.service.ChatMessageQueryUseCase;
import com.boardgo.domain.chatting.service.ChatRoomQueryUseCase;
import com.boardgo.domain.chatting.service.response.ChattingListResponse;
import com.boardgo.domain.chatting.service.response.LatestMessageResponse;
import com.boardgo.domain.mapper.ChatMapper;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomQueryFacadeImpl implements ChatRoomQueryFacade {
    private final ChatMapper chatMapper;

    private final MeetingQueryUseCase meetingQueryUseCase;
    private final ChatRoomQueryUseCase chatRoomQueryUseCase;
    private final ChatMessageQueryUseCase chatMessageQueryUseCase;
    private final MeetingParticipantQueryUseCase meetingParticipantQueryUseCase;

    @Override
    public List<ChattingListResponse> getList(Long userId) {
        List<Long> meetingIdList =
                meetingParticipantQueryUseCase.getMeetingIdByNotEqualsOut(userId);
        List<MeetingEntity> meetingList = meetingQueryUseCase.findByIdIn(meetingIdList);
        List<ChatRoomEntity> chatRoomList = chatRoomQueryUseCase.findByMeetingIdIn(meetingIdList);

        log.info("meetingIdList : {}", meetingList);
        log.info("chatRoomList : {}", chatRoomList);

        Map<Long, Long> chatRoomMap =
                chatRoomList.stream()
                        .collect(
                                Collectors.toMap(
                                        ChatRoomEntity::getMeetingId, ChatRoomEntity::getId));

        Map<Long, ChatMessage> chatMessageMap = getChatMessageMap(chatRoomList);
        for (Long key : chatMessageMap.keySet()) {
            log.info("key = {}", key);
            log.info("chatRoomMap.getRoomId(key) = {}", chatMessageMap.get(key).getRoomId());
            log.info("chatRoomMap.getContent(key) = {}", chatMessageMap.get(key).getContent());
        }

        return chatMapper.toChattingListResponseList(meetingList, chatMessageMap, chatRoomMap);
    }

    private Map<Long, ChatMessage> getChatMessageMap(List<ChatRoomEntity> chatRoomList) {
        return chatMessageQueryUseCase
                .getLast(chatRoomList.stream().map(ChatRoomEntity::getId).toList())
                .stream()
                .collect(
                        Collectors.toMap(
                                LatestMessageResponse::roomId,
                                LatestMessageResponse::latestMessage));
    }
}
