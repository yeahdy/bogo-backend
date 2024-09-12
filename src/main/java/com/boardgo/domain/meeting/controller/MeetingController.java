package com.boardgo.domain.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.common.utils.SecurityUtils.*;

import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.controller.request.MeetingUpdateRequest;
import com.boardgo.domain.meeting.service.facade.MeetingCommandFacade;
import com.boardgo.domain.meeting.service.facade.MeetingQueryFacade;
import com.boardgo.domain.meeting.service.response.MeetingResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchPageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final MeetingCommandFacade meetingCommandFacade;
    private final MeetingQueryFacade meetingQueryFacade;

    @PostMapping(value = "/meeting", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> create(
            @RequestPart(value = "meetingCreateRequest") @Valid
                    MeetingCreateRequest meetingCreateRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Long meetingId =
                meetingCommandFacade.create(meetingCreateRequest, imageFile, currentUserId());
        return ResponseEntity.created(URI.create("/meeting/" + meetingId)).build();
    }

    @GetMapping(value = "/meeting", headers = API_VERSION_HEADER1)
    public ResponseEntity<Page<MeetingSearchPageResponse>> search(
            @ModelAttribute @Valid MeetingSearchRequest meetingSearchRequest) {
        Page<MeetingSearchPageResponse> searchResult =
                meetingQueryFacade.search(meetingSearchRequest, currentUserIdWithoutThrow());
        if (searchResult.getSize() == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(searchResult);
    }

    @GetMapping(value = "/meeting/{id}", headers = API_VERSION_HEADER1)
    public ResponseEntity<MeetingResponse> getById(@PathVariable("id") @Positive Long id) {
        MeetingResponse meetingDetail =
                meetingQueryFacade.getDetailById(id, currentUserIdWithoutThrow());
        meetingCommandFacade.incrementViewCount(meetingDetail.meetingId());
        return ResponseEntity.ok(meetingDetail);
    }

    @PatchMapping(value = "/meeting", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> update(
            @RequestPart(value = "meetingUpdateRequest") MeetingUpdateRequest request,
            @RequestPart(value = "image") MultipartFile imageFile) {
        meetingCommandFacade.updateMeeting(request, currentUserId(), imageFile);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/meeting/share/{id}", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> incrementShareCount(@PathVariable("id") @Positive Long id) {
        meetingCommandFacade.incrementShareCount(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(value = "/meeting/complete/{id}", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> updateCompleteMeetingState(@PathVariable("id") @Positive Long id) {
        meetingCommandFacade.updateCompleteMeetingState(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/meeting/{id}", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> deleteMeeting(@PathVariable("id") @Positive Long id) {
        meetingCommandFacade.deleteMeeting(id, currentUserId());
        return ResponseEntity.ok().build();
    }
}
