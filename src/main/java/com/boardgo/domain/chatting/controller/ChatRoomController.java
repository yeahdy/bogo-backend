package com.boardgo.domain.chatting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;

import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.chatting.controller.request.ChatRequest;
import com.boardgo.domain.chatting.service.ChatMessageQueryUseCase;
import com.boardgo.domain.chatting.service.facade.ChatRoomQueryFacade;
import com.boardgo.domain.chatting.service.response.ChatMessageResponse;
import com.boardgo.domain.chatting.service.response.ChattingListResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomQueryFacade chatRoomQueryFacade;
    private final ChatMessageQueryUseCase chatMessageQueryUseCase;

    @GetMapping(value = "/chatroom/list", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<ChattingListResponse>> getChattingList() {
        List<ChattingListResponse> result =
                chatRoomQueryFacade.getList(SecurityUtils.currentUserId());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/chatroom", headers = API_VERSION_HEADER1)
    public ResponseEntity<Page<ChatMessageResponse>> getChattingHistory(
            @Valid ChatRequest chatRequest) {
        Page<ChatMessageResponse> result =
                chatMessageQueryUseCase.getChatHistory(
                        chatRequest.chatRoomId(), chatRequest.page());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}
