package com.boardgo.integration.review.service;

import static com.boardgo.domain.review.entity.EvaluationType.POSITIVE;
import static com.boardgo.integration.fixture.EvaluationTagFixture.getEvaluationTagEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.review.controller.dto.EvaluationTagListResponse;
import com.boardgo.domain.review.controller.dto.EvaluationTagResponse;
import com.boardgo.domain.review.repository.EvaluationTagRepository;
import com.boardgo.domain.review.service.EvaluationTagUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EvaluationTagQueryServiceV1Test extends IntegrationTestSupport {

    @Autowired private EvaluationTagUseCase evaluationTagUseCase;
    @Autowired private EvaluationTagRepository evaluationTagRepository;

    @Test
    @DisplayName("평가태그는 긍정적 태그와 부정적 태그로 분류된다")
    void 평가태그는_긍정적_태그와_부정적_태그로_분류된다() {
        // given
        evaluationTagRepository.saveAll(getEvaluationTagEntity());

        // when
        EvaluationTagListResponse evaluationTags = evaluationTagUseCase.getEvaluationTags();

        // then
        assertThat(evaluationTags.positiveTags()).isNotNull();
        assertThat(evaluationTags.negativeTags()).isNotNull();

        List<EvaluationTagResponse> positiveEvaluationTag = evaluationTags.positiveTags();
        for (EvaluationTagResponse tag : positiveEvaluationTag) {
            assertThat(tag.evaluationType()).isEqualTo(POSITIVE);
        }
    }
}
