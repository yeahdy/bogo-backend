package com.boardgo.integration.init;

import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.MeetingState;
import com.boardgo.domain.meeting.entity.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.service.MeetingCreateFactory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
public class TestMeetingInitializer {

    private final MeetingCreateFactory meetingCreateFactory;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final GameGenreMatchRepository gameGenreMatchRepository;

    public TestMeetingInitializer(
            MeetingCreateFactory meetingCreateFactory,
            MeetingParticipantRepository meetingParticipantRepository,
            GameGenreMatchRepository gameGenreMatchRepository) {
        this.meetingCreateFactory = meetingCreateFactory;
        this.meetingParticipantRepository = meetingParticipantRepository;
        this.gameGenreMatchRepository = gameGenreMatchRepository;
    }

    /**
     * 주의!! TestBoardGameInitializer.generateBoardGameData TestUserInfoInitializer.generateUserData
     * 에 의존적인 메서드입니다.
     */
    public void generateMeetingData() {
        MeetingMapper meetingMapper = MeetingMapper.INSTANCE;

        for (int i = 0; i < 30; i++) {
            int limitNumber = Math.max(i % 10, 2);

            long userId = (long) (i % 30) + 1;
            long rotationNumber = (i % 10) + 1L;

            List<Long> boardGameIdList = List.of(rotationNumber);
            List<Long> genreIdList = gameGenreMatchRepository.findByBoardGameIdIn(boardGameIdList);
            MeetingEntity meetingEntity =
                    meetingMapper.toMeetingEntity(
                            new MeetingCreateRequest(
                                    "content" + rotationNumber,
                                    "FREE",
                                    limitNumber,
                                    "title" + rotationNumber,
                                    "city" + limitNumber,
                                    "county" + limitNumber,
                                    i + ".12321321",
                                    i + ".787878",
                                    LocalDateTime.now().plusDays(rotationNumber),
                                    boardGameIdList,
                                    genreIdList),
                            userId,
                            "thumbnail" + i);
            Long savedMeetingId =
                    meetingCreateFactory.create(
                            meetingEntity, userId, boardGameIdList, genreIdList);

            int participantLimit = Math.max(i % limitNumber, 1);

            for (int j = 0; j < participantLimit; j++) {
                MeetingParticipantEntity.MeetingParticipantEntityBuilder builder =
                        MeetingParticipantEntity.builder();
                if (j == 0) {
                    builder.type(ParticipantType.LEADER);
                } else {
                    builder.type(ParticipantType.PARTICIPANT);
                }
                meetingParticipantRepository.save(
                        builder.meetingId(savedMeetingId)
                                .userInfoId((userId + j) % 30 + 1)
                                .build());
            }
        }
    }

    /** 모임 상태 여러 상태로 바꾸고 싶을 때 사용 * */
    private static void getMeetingState(int i) {
        MeetingState state;
        if (i % 2 == 0) {
            state = MeetingState.PROGRESS;
        } else if (i % 3 == 1) {
            state = MeetingState.COMPLETE;
        } else {
            state = MeetingState.FINISH;
        }
    }
}
