package com.boardgo.domain.termsconditions.controller;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.termsconditions.service.TermsConditionsQueryUseCase;
import com.boardgo.domain.termsconditions.service.response.TermsConditionsResponse;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms-conditions")
@Validated
public class TermsConditionsController {

    private final TermsConditionsQueryUseCase termsConditionsQueryUseCase;

    @GetMapping("")
    public ResponseEntity<List<TermsConditionsResponse>> getTermsConditionsList(
            @RequestParam("required") @NotEmpty(message = "빈 배열일 수 없습니다.") List<Boolean> required) {
        List<TermsConditionsResponse> termsConditionsList =
                termsConditionsQueryUseCase.getTermsConditionsList(required);
        if (Objects.isNull(termsConditionsList)) {
            throw new CustomNoSuchElementException("약관동의");
        }
        return ResponseEntity.ok(termsConditionsList);
    }

    // TODO. 약관 추가 및 히스토리 버전업데이트 API 개발
}
