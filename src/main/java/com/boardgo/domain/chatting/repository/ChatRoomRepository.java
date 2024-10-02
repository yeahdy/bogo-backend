package com.boardgo.domain.chatting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boardgo.domain.chatting.entity.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
	List<ChatRoomEntity> findByMeetingIdIn(List<Long> meetingIds);
}
