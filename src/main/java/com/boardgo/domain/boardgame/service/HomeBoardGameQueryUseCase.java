package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.enums.SituationType;
import com.boardgo.domain.boardgame.service.response.CumulativePopularityResponse;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import java.util.List;

public interface HomeBoardGameQueryUseCase {

    List<SituationBoardGameResponse> getSituationBoardGame(SituationType situationType);

    List<CumulativePopularityResponse> getCumulativePopularity();
}
