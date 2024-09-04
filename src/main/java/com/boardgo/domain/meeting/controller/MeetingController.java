package com.boardgo.domain.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;

import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.repository.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.repository.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.MeetingCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingCommandUseCase meetingCommandUseCase;
    private final MeetingQueryUseCase meetingQueryUseCase;

    @PostMapping(value = "/meeting", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> create(
            @RequestPart(value = "meetingCreateRequest") @Valid
                    MeetingCreateRequest meetingCreateRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Long meetingId = meetingCommandUseCase.create(meetingCreateRequest, imageFile);
        return ResponseEntity.created(URI.create("/meeting/" + meetingId)).build();
    }

    @GetMapping(value = "/meeting", headers = API_VERSION_HEADER1)
    public ResponseEntity<Page<MeetingSearchResponse>> search(
            @ModelAttribute @Valid MeetingSearchRequest meetingSearchRequest) {
        Page<MeetingSearchResponse> searchResult = meetingQueryUseCase.search(meetingSearchRequest);
        if (searchResult.getSize() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(searchResult);
    }

    @GetMapping(value = "/meeting/{id}", headers = API_VERSION_HEADER1)
    public ResponseEntity<MeetingDetailResponse> getById(@PathVariable("id") @Positive Long id) {
        MeetingDetailResponse meetingDetail = meetingQueryUseCase.getDetailById(id);
        meetingCommandUseCase.incrementViewCount(meetingDetail.meetingId());
        return ResponseEntity.ok(meetingDetail);
    }

    @PatchMapping(value = "/meeting/share/{id}", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> incrementShareCount(@PathVariable("id") @Positive Long id) {
        meetingCommandUseCase.incrementShareCount(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/meeting/complete/{id}", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> updateCompleteMeetingState(@PathVariable("id") @Positive Long id) {
        meetingCommandUseCase.updateCompleteMeetingState(id);
        return ResponseEntity.ok().build();
    }
}
