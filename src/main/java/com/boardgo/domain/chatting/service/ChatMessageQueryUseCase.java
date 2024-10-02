package com.boardgo.domain.chatting.service;

import java.util.List;

import com.boardgo.domain.chatting.service.response.LatestMessageResponse;

public interface ChatMessageQueryUseCase {
	List<LatestMessageResponse> getLast(List<Long> roomIdList);
}
