package com.boardgo.domain.boardgame.controller.request;

import com.boardgo.common.validator.annotation.ListInStringNotEmpty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record BoardGameCreateRequest(
        @NotEmpty String title,
        @PositiveOrZero Integer minPeople,
        @PositiveOrZero Integer maxPeople,
        @Positive Integer minPlaytime,
        @Positive Integer maxPlaytime,
        @ListInStringNotEmpty List<String> genres,
        @NotNull MultipartFile imageFile) {}
