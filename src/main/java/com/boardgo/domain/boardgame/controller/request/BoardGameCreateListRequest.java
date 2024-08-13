package com.boardgo.domain.boardgame.controller.request;

import java.util.List;

public record BoardGameCreateListRequest(List<BoardGameCreateRequest> boardGameCreateRequestList) {}
