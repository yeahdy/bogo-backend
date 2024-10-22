package com.boardgo.integration.chat.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

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
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

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

    @Test
    @DisplayName("사용자는 이전 채팅 목록을 조회할 수 있다")
    void 사용자는_이전_채팅_목록을_조회할_수_있다() {
        // given
        chatRepository.deleteByRoomId(1L);
        for (int i = 0; i < 30; i++) {
            chatRepository.save(
                    ChatMessage.builder()
                            .roomId(1L)
                            .userId(1L)
                            .content("content" + i)
                            .sendDatetime(LocalDateTime.now())
                            .build());
        }
        // when
        // then
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .queryParam("chatRoomId", 1L)
                .queryParam("page", 0)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(
                        document(
                                "chatroom-history",
                                queryParameters(
                                        parameterWithName("chatRoomId").description("채팅방 ID"),
                                        parameterWithName("page").description("페이지 0부터 시작")),
                                responseFields(
                                        fieldWithPath("content[].roomId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("채팅방 ID"),
                                        fieldWithPath("content[].userId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("유저 ID"),
                                        fieldWithPath("content[].content")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅방 메시지")
                                                .optional(),
                                        fieldWithPath("content[].sendDatetime")
                                                .type(JsonFieldType.STRING)
                                                .description("채팅 보낸 날짜")
                                                .optional(),
                                        fieldWithPath("totalElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("전체 개수"),
                                        fieldWithPath("totalPages")
                                                .type(JsonFieldType.NUMBER)
                                                .description("전체 페이지 수"),
                                        fieldWithPath("number")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지"),
                                        fieldWithPath("size")
                                                .type(JsonFieldType.NUMBER)
                                                .description("한 페이지 당 개수"),
                                        fieldWithPath("sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 조건이 비어있는지 여부"),
                                        fieldWithPath("sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("content가 정렬되어 있는지의 여부"),
                                        fieldWithPath("sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬이 안되어있는지의 여부"),
                                        fieldWithPath("first")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("가장 첫번째 페이지의 숫자"),
                                        fieldWithPath("last")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("가장 마지막 페이지의 숫자"),
                                        fieldWithPath("numberOfElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지에 있는 content.size()"),
                                        fieldWithPath("empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 비어있는지 여부"),
                                        fieldWithPath("pageable.pageNumber")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("pageable.pageSize")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 당 항목 수"),
                                        fieldWithPath("pageable.offset")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 시작 지점"),
                                        fieldWithPath("pageable.paged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 페이징 되었는지 여부"),
                                        fieldWithPath("pageable.unpaged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 페이징 되지 않았는지 여부"),
                                        fieldWithPath("pageable.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 조건이 비어있는지 여부"),
                                        fieldWithPath("pageable.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬이 되었는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬되지 않았는지 여부"))))
                .when()
                .get("/chatroom")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
