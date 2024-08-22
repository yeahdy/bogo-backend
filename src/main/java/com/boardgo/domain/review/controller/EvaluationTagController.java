package com.boardgo.domain.review.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;

import com.boardgo.domain.review.controller.dto.EvaluationTagListResponse;
import com.boardgo.domain.review.service.EvaluationTagUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EvaluationTagController {

    private final EvaluationTagUseCase evaluationTagUseCase;

    @GetMapping(value = "/evaluation-tags", headers = API_VERSION_HEADER1)
    public ResponseEntity<EvaluationTagListResponse> getEvaluationTags() {
        return ResponseEntity.ok(evaluationTagUseCase.getEvaluationTags());
    }
}
