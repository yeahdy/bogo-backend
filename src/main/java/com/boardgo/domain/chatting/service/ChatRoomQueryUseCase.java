package com.boardgo.domain.chatting.service;

import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import java.util.List;

public interface ChatRoomQueryUseCase {
    List<ChatRoomEntity> findByMeetingIdIn(List<Long> meetingIds);
}
