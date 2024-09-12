package com.boardgo.domain.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;

import com.boardgo.domain.meeting.service.facade.MeetingQueryFacade;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeMeetingController {

    private final MeetingQueryFacade meetingQueryFacade;

    @GetMapping(value = "/deadline", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<HomeMeetingDeadlineResponse>> getMeetingDeadlines() {
        List<HomeMeetingDeadlineResponse> homeMeetingDeadlines =
                meetingQueryFacade.getMeetingDeadlines();
        if (homeMeetingDeadlines.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().body(homeMeetingDeadlines);
    }
}
