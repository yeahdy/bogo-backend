package com.boardgo.domain.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.common.utils.SecurityUtils.*;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardgo.domain.meeting.controller.request.MeetingOutRequest;
import com.boardgo.domain.meeting.controller.request.MeetingParticipateRequest;
import com.boardgo.domain.meeting.service.MeetingParticipantCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

    @GetMapping(value = "/{meetingId}", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<UserParticipantResponse>> getParticipants(@PathVariable("meetingId") Long meetingId) {
        return ResponseEntity.ok(meetingParticipantQueryUseCase.findByMeetingId(meetingId));
    }

    @GetMapping(value = "/out/{meetingId}", headers = API_VERSION_HEADER1)
    public ResponseEntity<ParticipantOutResponse> getOutState(
            @PathVariable("meetingId") Long meetingId) {
        return ResponseEntity.ok(meetingParticipantQueryUseCase.getOutState(meetingId));
    }

    @PatchMapping(
            value = {"/out/{userId}", "/out"},
            headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> outMeeting(
            @PathVariable(value = "userId", required = false) Long userId,
            @RequestBody @Valid MeetingOutRequest meetingOutRequest) {
        Long id = Objects.isNull(userId) ? currentUserId() : userId;
        meetingParticipantCommandUseCase.outMeeting(meetingOutRequest.meetingId(), id);
        return ResponseEntity.ok().build();
    }
}
