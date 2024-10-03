package com.boardgo.domain.chatting.service;

import com.boardgo.domain.chatting.service.response.LatestMessageResponse;
import java.util.List;

public interface ChatMessageQueryUseCase {
    List<LatestMessageResponse> getLast(List<Long> roomIdList);
}
