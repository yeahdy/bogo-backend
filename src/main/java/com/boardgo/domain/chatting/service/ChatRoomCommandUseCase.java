package com.boardgo.domain.chatting.service;

public interface ChatRoomCommandUseCase {
    Long create(Long meetingId);

    void deleteByMeetingId(Long meetingId);
}
