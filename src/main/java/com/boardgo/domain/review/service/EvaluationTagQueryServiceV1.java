package com.boardgo.domain.review.service;

import com.boardgo.domain.mapper.EvaluationTagMapper;
import com.boardgo.domain.review.entity.EvaluationTagEntity;
import com.boardgo.domain.review.entity.EvaluationType;
import com.boardgo.domain.review.repository.EvaluationTagRepository;
import com.boardgo.domain.review.service.response.EvaluationTagListResponse;
import com.boardgo.domain.review.service.response.EvaluationTagResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationTagQueryServiceV1 implements EvaluationTagUseCase {

    private final EvaluationTagRepository evaluationTagRepository;
    private final EvaluationTagMapper evaluationTagMapper;

    @Override
    public EvaluationTagListResponse getEvaluationTags() {
        List<EvaluationTagEntity> evaluationTagEntities =
                evaluationTagRepository.findAllByOrderById();
        Map<EvaluationType, List<EvaluationTagEntity>> evaluationTypeMap =
                evaluationTagEntities.stream()
                        .collect(Collectors.groupingBy(EvaluationTagEntity::getEvaluationType));

        List<EvaluationTagResponse> positiveTags = null;
        List<EvaluationTagResponse> negativeTags = null;
        for (EvaluationType evaluationType : EvaluationType.values()) {
            List<EvaluationTagResponse> evaluationTagResult =
                    evaluationTagMapper.toEvaluationTagResponseList(
                            evaluationTypeMap.get(evaluationType));

            switch (evaluationType) {
                case POSITIVE -> positiveTags = evaluationTagResult;
                case NEGATIVE -> negativeTags = evaluationTagResult;
            }
        }

        return new EvaluationTagListResponse(positiveTags, negativeTags);
    }
}
