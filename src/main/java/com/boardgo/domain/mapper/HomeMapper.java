package com.boardgo.domain.mapper;

import com.boardgo.domain.boardgame.repository.projection.CumulativePopularityProjection;
import com.boardgo.domain.boardgame.repository.projection.SituationBoardGameProjection;
import com.boardgo.domain.boardgame.service.response.CumulativePopularityResponse;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HomeMapper {

    HomeMapper INSTANCE = Mappers.getMapper(HomeMapper.class);

    @Mapping(target = "genres", source = "genre")
    SituationBoardGameResponse toSituationBoardGameResponse(
            SituationBoardGameProjection situationBoardGameProjection);

    default List<String> listMapping(String value) {
        if (value != null && !value.isEmpty()) {
            return new ArrayList<>(Arrays.asList(value.split(",")));
        }
        return Collections.emptyList();
    }

    CumulativePopularityResponse toCumulativePopularityResponse(
            CumulativePopularityProjection projection, Long cumulativeCount);
}
