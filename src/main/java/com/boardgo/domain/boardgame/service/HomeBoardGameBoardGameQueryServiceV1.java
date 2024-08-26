package com.boardgo.domain.boardgame.service;

import com.boardgo.domain.boardgame.entity.enums.SituationType;
import com.boardgo.domain.boardgame.repository.BoardGameRepository;
import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityCountProjection;
import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityProjection;
import com.boardgo.domain.boardgame.repository.projection.SituationBoardGameProjection;
import com.boardgo.domain.boardgame.service.response.CumulativePopularityResponse;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import com.boardgo.domain.mapper.HomeMapper;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public List<CumulativePopularityResponse> getCumulativePopularity() {
        final int LIMIT_RANK = 7;
        List<CumulativePopularityCountProjection> cumulativePopularityCount =
                meetingRepository.findCumulativePopularityBoardGameRank(LIMIT_RANK);
        Map<Long, Long> rankMap =
                cumulativePopularityCount.stream()
                        .collect(
                                Collectors.toMap(
                                        CumulativePopularityCountProjection::boardGameId,
                                        CumulativePopularityCountProjection::cumulativeCount));

        List<CumulativePopularityProjection> boardGames =
                meetingRepository.findBoardGameOrderByRank(rankMap.keySet());
        List<CumulativePopularityResponse> cumulativePopularityList = new ArrayList<>();

        for (CumulativePopularityProjection game : boardGames) {
            Long boardGameId = game.boardGameId();
            CumulativePopularityResponse cumulativePopularity =
                    homeMapper.toCumulativePopularityResponse(game, rankMap.get(boardGameId));
            cumulativePopularityList.add(cumulativePopularity);
        }

        Collections.sort(
                cumulativePopularityList,
                Comparator.comparingLong(CumulativePopularityResponse::cumulativeCount).reversed());
        return cumulativePopularityList;
    }
}
