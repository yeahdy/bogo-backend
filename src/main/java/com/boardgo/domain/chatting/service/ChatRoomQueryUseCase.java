package com.boardgo.domain.chatting.service;

import java.util.List;

import com.boardgo.domain.chatting.entity.ChatRoomEntity;

public interface ChatRoomQueryUseCase {
	List<ChatRoomEntity> findByMeetingIdIn(List<Long> meetingIds);
}
