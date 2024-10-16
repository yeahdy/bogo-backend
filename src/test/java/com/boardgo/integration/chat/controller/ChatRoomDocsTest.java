package com.boardgo.integration.chat.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.boardgo.domain.chatting.entity.ChatMessage;
import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import com.boardgo.domain.chatting.repository.ChatRepository;
import com.boardgo.domain.chatting.repository.ChatRoomRepository;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.data.MeetingData;
import com.boardgo.integration.data.UserInfoData;
import com.boardgo.integration.support.RestDocsTestSupport;

public class ChatRoomDocsTest extends RestDocsTestSupport {
    @Autowired private UserRepository userRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private ChatRepository chatRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;

    @Test
    @DisplayName("사용자는 채팅 목록을 조회할 수 있다")
    void 사용자는_채팅_목록을_조회할_수_있다() {
        // given
        UserInfoEntity savedUser =
                userRepository.save(
                        UserInfoData.userInfoEntityData("test@test.com", "test").build());
        ChatRoomEntity savedChatRoom = getChatRoomEntity(savedUser);
        ChatRoomEntity savedChatRoom2 = getChatRoomEntity(savedUser);
        ChatMessage chat1 =
                chatRepository.save(
                        ChatMessage.builder()
                                .roomId(savedChatRoom.getId())
                                .userId(savedUser.getId())
                                .content("hi Test")
                                .sendDatetime(LocalDateTime.now())
                                .build());
        LocalDateTime now = LocalDateTime.now();
        ChatMessage chat2 =
                chatRepository.save(
                        ChatMessage.builder()
                                .roomId(savedChatRoom.getId())
                                .userId(savedUser.getId())
                                .content("hi Test")
                                .sendDatetime(now)
                                .build());
        List<ChatMessage> room1 = chatRepository.findByRoomId(1L);
        // when
        // then
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(
                        document(
                                "chatroom-list",
                                responseFields(
                                        fieldWithPath("[].chatRoomId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("채팅방 id"),
                                        fieldWithPath("[].meetingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 id"),
                                        fieldWithPath("[].thumbnail")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 썸네일"),
                                        fieldWithPath("[].meetingTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 제목"),
                                        fieldWithPath("[].lastMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅방 마지막 메시지")
                                                .optional(),
                                        fieldWithPath("[].lastSendDatetime")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅방 마지막 날짜")
                                                .optional())))
                .when()
                .get("/chatroom/list")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private ChatRoomEntity getChatRoomEntity(UserInfoEntity savedUser) {
        MeetingEntity savedMeeting =
                meetingRepository.save(MeetingData.getMeetingEntityData(savedUser.getId()).build());
        System.out.println("savedMeetingId : " + savedMeeting.getId());
        meetingParticipantRepository.save(
                MeetingParticipantEntity.builder()
                        .meetingId(savedMeeting.getId())
                        .userInfoId(savedUser.getId())
                        .type(ParticipantType.LEADER)
                        .build());
        return chatRoomRepository.save(
                ChatRoomEntity.builder().meetingId(savedMeeting.getId()).build());
    }
}
