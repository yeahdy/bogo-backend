package com.boardgo.domain.chatting.service;

import org.springframework.stereotype.Service;

import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import com.boardgo.domain.chatting.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomCommandServiceV1 implements ChatRoomCommandUseCase{
	private final ChatRoomRepository chatRoomRepository;

	@Override
	public Long create(Long meetingId) {
		ChatRoomEntity chatRoom = ChatRoomEntity.builder().meetingId(meetingId).build();
		return chatRoomRepository.save(chatRoom).getId();
	}

	@Override
	public void deleteByMeetingId(Long meetingId) {
		chatRoomRepository.deleteById(meetingId);
	}
}
