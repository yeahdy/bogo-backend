package com.boardgo.domain.chatting.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import com.boardgo.domain.chatting.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomQueryServiceV1 implements ChatRoomQueryUseCase{

	private final ChatRoomRepository chatRoomRepository;

	@Override
	public List<ChatRoomEntity> findByMeetingIdIn(List<Long> meetingIds) {
		return chatRoomRepository.findByMeetingIdIn(meetingIds);
	}
}
