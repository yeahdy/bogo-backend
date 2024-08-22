package com.boardgo.domain.mapper;

import com.boardgo.domain.review.controller.dto.EvaluationTagResponse;
import com.boardgo.domain.review.entity.EvaluationTagEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EvaluationTagMapper {

    EvaluationTagMapper INSTANCE = Mappers.getMapper(EvaluationTagMapper.class);

    List<EvaluationTagResponse> toEvaluationTagResponseList(
            List<EvaluationTagEntity> evaluationTagEntities);

    @Mapping(source = "id", target = "evaluationTagId")
    EvaluationTagResponse toEvaluationTagResponse(EvaluationTagEntity evaluationTagEntity);
}
