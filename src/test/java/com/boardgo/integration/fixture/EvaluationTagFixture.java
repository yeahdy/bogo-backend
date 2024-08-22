package com.boardgo.integration.fixture;

import static com.boardgo.domain.review.entity.EvaluationType.NEGATIVE;
import static com.boardgo.domain.review.entity.EvaluationType.POSITIVE;

import com.boardgo.domain.review.entity.EvaluationTagEntity;
import com.boardgo.domain.review.entity.EvaluationType;
import java.util.ArrayList;
import java.util.List;

public abstract class EvaluationTagFixture {

    public static List<EvaluationTagEntity> getEvaluationTagEntity() {
        List<EvaluationTagEntity> tagEntityList = new ArrayList<>();
        tagEntityList.add(getEvaluationTagEntity(POSITIVE, "시간을 잘 지켜요"));
        tagEntityList.add(getEvaluationTagEntity(POSITIVE, "매너가 좋아요"));
        tagEntityList.add(getEvaluationTagEntity(POSITIVE, "재미있어요"));
        tagEntityList.add(getEvaluationTagEntity(POSITIVE, "공정해요"));
        tagEntityList.add(getEvaluationTagEntity(POSITIVE, "보드게임의 신!"));
        tagEntityList.add(getEvaluationTagEntity(POSITIVE, "다시 만나고 싶어요!"));
        tagEntityList.add(getEvaluationTagEntity(NEGATIVE, "시간을 잘 지켜요"));
        tagEntityList.add(getEvaluationTagEntity(NEGATIVE, "비매너 플레이어"));
        tagEntityList.add(getEvaluationTagEntity(NEGATIVE, "재미없어요"));
        tagEntityList.add(getEvaluationTagEntity(NEGATIVE, "의도가 부적절해요"));
        tagEntityList.add(getEvaluationTagEntity(NEGATIVE, "공정하지 못해요"));
        tagEntityList.add(getEvaluationTagEntity(NEGATIVE, "다시 만나고 싶지 않아요!"));

        return tagEntityList;
    }

    private static EvaluationTagEntity getEvaluationTagEntity(
            EvaluationType evaluationType, String tagPhrase) {
        return EvaluationTagEntity.builder()
                .evaluationType(evaluationType)
                .tagPhrase(tagPhrase)
                .build();
    }
}
