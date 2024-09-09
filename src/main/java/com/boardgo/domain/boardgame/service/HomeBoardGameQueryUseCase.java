package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.enums.SituationType;
import com.boardgo.domain.boardgame.service.response.CumulativePopularityResponse;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import java.util.List;

// FIXME HomeBoardGameQueryFacade 로 변경하기 + 테스트코드 작성
public interface HomeBoardGameQueryUseCase {

    List<SituationBoardGameResponse> getSituationBoardGame(SituationType situationType);

    List<CumulativePopularityResponse> getCumulativePopularity();
}
