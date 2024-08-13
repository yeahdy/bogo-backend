package com.boardgo.domain.boardgame.controller.request;

public record BoardGameSearchRequest(Long count, String searchWord, Integer page, Integer size) {}
