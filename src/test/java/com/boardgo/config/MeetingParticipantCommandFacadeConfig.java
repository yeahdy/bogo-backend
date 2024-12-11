package com.boardgo.config;

import static org.mockito.Mockito.mock;

import com.boardgo.domain.meeting.service.MeetingParticipantCommandUseCase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class MeetingParticipantCommandFacadeConfig {

    @Bean
    @Primary
    public MeetingParticipantCommandUseCase meetingParticipantCommandUseCase() {
        return mock(MeetingParticipantCommandUseCase.class);
    }
}
