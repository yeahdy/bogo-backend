package com.boardgo.integration.chat.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boardgo.domain.chatting.entity.ChatMessage;
import com.boardgo.domain.chatting.entity.ChatRoomEntity;
import com.boardgo.domain.chatting.repository.ChatRepository;
import com.boardgo.domain.chatting.repository.ChatRoomRepository;
import com.boardgo.domain.chatting.service.facade.ChatRoomQueryFacade;
import com.boardgo.domain.chatting.service.response.ChattingListResponse;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.data.MeetingData;
import com.boardgo.integration.data.UserInfoData;
import com.boardgo.integration.support.IntegrationTestSupport;

public class ChatRoomQueryFacadeTest extends IntegrationTestSupport {
	@Autowired private UserRepository userRepository;
	@Autowired private ChatRoomRepository chatRoomRepository;
	@Autowired private ChatRepository chatRepository;
	@Autowired private MeetingRepository meetingRepository;
	@Autowired private MeetingParticipantRepository meetingParticipantRepository;
	@Autowired private ChatRoomQueryFacade chatRoomQueryFacade;

	@Test
	@DisplayName("채팅방 목록을 가져올 수 있다")
	void 채팅방_목록을_가져올_수_있다() {
	    //given
		UserInfoEntity savedUser = userRepository.save(UserInfoData.userInfoEntityData("test@test.com", "test").build());
		ChatRoomEntity savedChatRoom = getChatRoomEntity(savedUser);
		ChatRoomEntity savedChatRoom2 = getChatRoomEntity(savedUser);
		ChatMessage chat1 = chatRepository.save(ChatMessage.builder()
			.roomId(savedChatRoom.getId())
			.userId(savedUser.getId())
			.content("hi Test")
			.sendDatetime(LocalDateTime.now()).build());
		LocalDateTime now = LocalDateTime.now();
		ChatMessage chat2 = chatRepository.save(ChatMessage.builder()
			.roomId(savedChatRoom.getId())
			.userId(savedUser.getId())
			.content("hi Test2")
			.sendDatetime(now).build());
		//when
		List<ChattingListResponse> result = chatRoomQueryFacade.getList(savedUser.getId());
		//then
		assertThat(result).isNotEmpty();
		assertThat(result).extracting(ChattingListResponse::chatRoomId)
			.containsExactlyInAnyOrder(1L, 2L);
		assertThat(result).extracting(ChattingListResponse::lastMessage)
			.containsExactlyInAnyOrder(null, "hi Test2");
		assertThat(result.getFirst().lastSendDatetime().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(now.truncatedTo(ChronoUnit.SECONDS));
		assertThat(result.getFirst().chatRoomId()).isEqualTo(savedChatRoom.getId());
	}

	private ChatRoomEntity getChatRoomEntity(UserInfoEntity savedUser) {
		MeetingEntity savedMeeting = meetingRepository.save(MeetingData.getMeetingEntityData(savedUser.getId()).build());
		System.out.println("savedMeetingId : " + savedMeeting.getId());
		meetingParticipantRepository.save(MeetingParticipantEntity.builder()
			.meetingId(savedMeeting.getId())
			.userInfoId(savedUser.getId())
			.type(ParticipantType.LEADER)
			.build());
		return chatRoomRepository.save(ChatRoomEntity.builder()
			.meetingId(savedMeeting.getId()).build());
	}
}
