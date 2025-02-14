package com.boardgo.init;

import com.boardgo.domain.boardgame.repository.GameGenreMatchRepository;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingGameMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingGenreMatchRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Profile("local")
@Component
@RequiredArgsConstructor
public class MeetingInitializer implements ApplicationRunner {

    private final MeetingRepository meetingRepository;
    private final MeetingGenreMatchRepository meetingGenreMatchRepository;
    private final MeetingGameMatchRepository meetingGameMatchRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final GameGenreMatchRepository gameGenreMatchRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        MeetingMapper meetingMapper = MeetingMapper.INSTANCE;

        for (int i = 0; i < 300; i++) {
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
                                    "detailAddress",
                                    "location",
                                    LocalDateTime.now().plusDays(rotationNumber),
                                    boardGameIdList,
                                    genreIdList),
                            userId,
                            "thumbnail" + i);
            MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
            Long meetingId = savedMeeting.getId();
            meetingGenreMatchRepository.bulkInsert(genreIdList, meetingId);
            meetingGameMatchRepository.bulkInsert(boardGameIdList, meetingId);
            meetingParticipantRepository.save(
                    MeetingParticipantEntity.builder()
                            .userInfoId(userId)
                            .meetingId(meetingId)
                            .type(ParticipantType.LEADER)
                            .build());
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
                        builder.meetingId(meetingId).userInfoId((userId + j) % 30 + 1).build());
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
