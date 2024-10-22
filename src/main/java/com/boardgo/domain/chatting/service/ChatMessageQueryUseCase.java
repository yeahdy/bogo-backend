package com.boardgo.domain.chatting.service;

import com.boardgo.domain.chatting.service.response.ChatMessageResponse;
import com.boardgo.domain.chatting.service.response.LatestMessageResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ChatMessageQueryUseCase {
    List<LatestMessageResponse> getLast(List<Long> roomIdList);

    Page<ChatMessageResponse> getChatHistory(Long chatRoomId, int page);
}
