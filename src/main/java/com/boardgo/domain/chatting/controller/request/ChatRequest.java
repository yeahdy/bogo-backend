package com.boardgo.domain.chatting.controller.request;

import com.google.firebase.database.annotations.NotNull;
import jakarta.validation.constraints.Positive;

public record ChatRequest(@NotNull @Positive Long chatRoomId, int page) {}
