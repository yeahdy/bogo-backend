package com.boardgo.domain.termsconditions.controller;

import static com.boardgo.common.utils.SecurityUtils.currentUserId;

import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.termsconditions.service.facade.UserTermsConditionsCommandFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms-conditions")
@Validated
public class UserTermsConditionsController {

    private final UserTermsConditionsCommandFacade userTermsConditionsCommandFacade;

    @PostMapping("/user")
    public ResponseEntity<Void> createUserTermsConditions(
            @RequestBody @NotEmpty(message = "빈 배열일 수 없습니다.")
                    List<@Valid TermsConditionsCreateRequest> request) {
        userTermsConditionsCommandFacade.createUserTermsConditions(request, currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
