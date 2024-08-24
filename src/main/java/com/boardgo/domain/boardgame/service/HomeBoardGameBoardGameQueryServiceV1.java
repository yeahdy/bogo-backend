package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.SituationType;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.projection.SituationBoardGameProjection;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import com.boardgo.domain.mapper.HomeMapper;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeBoardGameBoardGameQueryServiceV1 implements HomeBoardGameQueryUseCase {

    private final BoardGameRepository boardGameRepository;
    private final MeetingRepository meetingRepository;
    private final HomeMapper homeMapper;

    @Override
    public List<SituationBoardGameResponse> getSituationBoardGame(SituationType situationType) {
        List<SituationBoardGameProjection> situationBoardGames =
                boardGameRepository.findByMaxPeopleBetween(situationType.getPeople());

        Map<String, SituationBoardGameResponse> boardGameMap = new HashMap<>();
        for (SituationBoardGameProjection gameDto : situationBoardGames) {
            boardGameMap.merge(
                    gameDto.title(),
                    homeMapper.toSituationBoardGameResponse(gameDto),
                    (existing, replacement) -> {
                        existing.genres().addAll(replacement.genres());
                        return existing;
                    });
        }
        return new ArrayList<>(boardGameMap.values());
    }

    /*
        public List<CumulativePopularityResponse> getCumulativePopularity() {
            List<CumulativePopularityCountProjection> cumulativePopularityCount =
                    meetingRepository.findCumulativePopularityBoardGameRank(7);

            List<Long> rankList = cumulativePopularityCount.stream()
                    .map(CumulativePopularityCountProjection::cumulativeCount)
                    .collect(Collectors.toList());
    //        Map<Long, Long> rankMap = cumulativePopularityCount.stream()
    //                .collect(Collectors.toMap(CumulativePopularityCountProjection::boardGameId
    //                        , CumulativePopularityCountProjection::cumulativeCount,
    //                        (existingValue, newValue) -> existingValue,
    //                        LinkedHashMap::new));

            List<CumulativePopularityResponse> boardGames =
                    meetingRepository.findBoardGameOrderByRank(rankList);

            for (CumulativePopularityResponse game : boardGames) {
                CumulativePopularityCountProjection cumulativeCount = cumulativePopularityCount.get(game.getRank());
                game.updateCumulativeCount(cumulativeCount.cumulativeCount());
                game.updateRank();
            }
            return boardGames;
        }
        */
}
