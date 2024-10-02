package com.boardgo.domain.chatting.service.facade;

import java.util.List;

import com.boardgo.domain.chatting.service.response.ChattingListResponse;

public interface ChatRoomQueryFacade {
	List<ChattingListResponse> getList(Long userId);
}
