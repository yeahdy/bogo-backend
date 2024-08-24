package com.boardgo.domain.boardgame.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;

import com.boardgo.domain.boardgame.entity.SituationType;
import com.boardgo.domain.boardgame.service.HomeBoardGameQueryUseCase;
import com.boardgo.domain.boardgame.service.response.SituationBoardGameResponse;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeBoardGameController {

    private final HomeBoardGameQueryUseCase homeBoardGameQueryUseCase;

    @GetMapping(value = "/situation", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<SituationBoardGameResponse>> getSituationBoardGame(
            @RequestParam("situationType") @NotNull SituationType situationType) {
        return ResponseEntity.ok()
                .body(homeBoardGameQueryUseCase.getSituationBoardGame(situationType));
    }

    //    @GetMapping(value = "/cumulative-popularity", headers = API_VERSION_HEADER1)
    //    public ResponseEntity<List<CumulativePopularityResponse>> getCumulativePopularity() {
    //        return ResponseEntity.ok().body(homeQueryUseCase.getCumulativePopularity());
    //    }
}
