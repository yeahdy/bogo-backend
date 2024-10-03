package com.boardgo.domain.chatting.service.facade;

import com.boardgo.domain.chatting.service.response.ChattingListResponse;
import java.util.List;

public interface ChatRoomQueryFacade {
    List<ChattingListResponse> getList(Long userId);
}
