package com.boardgo.domain.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.common.utils.SecurityUtils.*;

import com.boardgo.domain.meeting.controller.request.MyPageMeetingFilterRequest;
import com.boardgo.domain.meeting.service.facade.MyPageMeetingQueryFacade;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyPageMeetingController {
    private final MyPageMeetingQueryFacade myPageMeetingQueryFacade;

    @GetMapping(value = "/my/meeting", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<MeetingMyPageResponse>> getMyPageMeetingByFilter(
            @Valid MyPageMeetingFilterRequest filter) {
        List<MeetingMyPageResponse> result =
                myPageMeetingQueryFacade.findByFilter(filter.filter(), currentUserId());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/my/meeting/like", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<LikedMeetingMyPageResponse>> getLikeMeeting() {
        List<LikedMeetingMyPageResponse> result =
                myPageMeetingQueryFacade.findLikedMeeting(currentUserId());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }
}
