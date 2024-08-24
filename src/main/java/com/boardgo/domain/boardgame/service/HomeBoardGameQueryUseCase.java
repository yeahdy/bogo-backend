package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.SituationType;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import java.util.List;

public interface HomeBoardGameQueryUseCase {

    List<SituationBoardGameResponse> getSituationBoardGame(SituationType situationType);

    //    List<CumulativePopularityResponse> getCumulativePopularity();
}
