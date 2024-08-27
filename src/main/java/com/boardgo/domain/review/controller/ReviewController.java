package com.boardgo.domain.review.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;
import static com.boardgo.common.utils.SecurityUtils.currentUserId;

import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.entity.enums.ReviewType;
import com.boardgo.domain.review.service.ReviewUseCase;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/my/review")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewUseCase reviewUseCase;

    @GetMapping(value = "/meetings", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<ReviewMeetingResponse>> getReviewMeetings(
            @RequestParam("reviewType") ReviewType reviewType) {
        List<ReviewMeetingResponse> reviewMeetings =
                reviewUseCase.getReviewMeetings(reviewType, currentUserId());
        if (reviewMeetings.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reviewMeetings);
    }

    @PostMapping(value = "", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> create(@RequestBody @Valid ReviewCreateRequest createRequest) {
        reviewUseCase.create(createRequest, currentUserId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/meetings/{meetingId}", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<ReviewMeetingParticipantsResponse>> getReviewMeetingParticipants(
            @PathVariable("meetingId") @Positive Long meetingId) {
        List<ReviewMeetingParticipantsResponse> reviewMeetingParticipants =
                reviewUseCase.getReviewMeetingParticipants(meetingId, currentUserId());
        return ResponseEntity.ok(reviewMeetingParticipants);
    }
}
