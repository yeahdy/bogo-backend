package com.boardgo.domain.chatting.service;

import com.boardgo.domain.chatting.repository.ChatRepository;
import com.boardgo.domain.chatting.service.response.LatestMessageResponse;
import com.boardgo.domain.mapper.ChatMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageQueryServiceV1 implements ChatMessageQueryUseCase {

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<LatestMessageResponse> getLast(List<Long> roomIdList) {
        return chatMapper.toLatestMessageResponse(
                chatRepository.findLatestMessagesByRoomIds(roomIdList));
    }
}
