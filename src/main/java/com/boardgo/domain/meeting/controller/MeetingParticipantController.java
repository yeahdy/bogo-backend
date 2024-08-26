package com.boardgo.domain.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.common.utils.SecurityUtils.*;

import com.boardgo.domain.meeting.controller.request.MeetingParticipateRequest;
import com.boardgo.domain.meeting.service.MeetingParticipantCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meeting-participant")
@RequiredArgsConstructor
public class MeetingParticipantController {
    private final MeetingParticipantQueryUseCase meetingParticipantQueryUseCase;
    private final MeetingParticipantCommandUseCase meetingParticipantCommandUseCase;

    @PostMapping(value = "/participation", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> participateMeeting(
            @RequestBody @Valid MeetingParticipateRequest participateRequest) {
        meetingParticipantCommandUseCase.participateMeeting(participateRequest, currentUserId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/out/{meetingId}", headers = API_VERSION_HEADER1)
    public ResponseEntity<ParticipantOutResponse> getOutState(
            @PathVariable("meetingId") Long meetingId) {
        return ResponseEntity.ok(meetingParticipantQueryUseCase.getOutState(meetingId));
    }
}
