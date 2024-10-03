package com.boardgo.domain.chatting.repository;

import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    List<ChatRoomEntity> findByMeetingIdIn(List<Long> meetingIds);
}
