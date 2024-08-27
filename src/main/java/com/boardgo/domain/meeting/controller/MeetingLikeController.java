package com.boardgo.domain.meeting.controller;

import com.boardgo.domain.meeting.service.MeetingLikeCommandUseCase;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class MeetingLikeController {
    private final MeetingLikeCommandUseCase meetingLikeCommandUseCase;

    @PostMapping("/meeting/like")
    public ResponseEntity<Void> likeMeetings(
            @RequestParam("meetingIdList") List<Long> meetingIdList) {
        meetingLikeCommandUseCase.createMany(meetingIdList);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/meeting/like/{meetingId}")
    public ResponseEntity<Void> deleteLike(@PathVariable("meetingId") @Positive Long meetingId) {
        meetingLikeCommandUseCase.deleteByMeetingId(meetingId);
        return ResponseEntity.ok().build();
    }
}
