package com.boardgo.domain.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;

import com.boardgo.domain.meeting.controller.dto.MeetingCreateRequest;
import com.boardgo.domain.meeting.service.MeetingCommandUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MeetingController {
    private final MeetingCommandUseCase meetingCommandUseCase;

    @PostMapping(
            value = "/meeting",
            headers = API_VERSION_HEADER1,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> create(
            @RequestPart(value = "meetingCreateRequest") @Valid
                    MeetingCreateRequest meetingCreateRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        Long meetingId = meetingCommandUseCase.create(meetingCreateRequest, imageFile);
        return ResponseEntity.created(URI.create("/meeting/" + meetingId)).build();
    }
}
