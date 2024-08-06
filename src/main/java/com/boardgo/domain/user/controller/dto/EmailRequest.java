package com.boardgo.domain.user.controller.dto;

import jakarta.validation.constraints.Email;

public record EmailRequest(@Email String email) {}
