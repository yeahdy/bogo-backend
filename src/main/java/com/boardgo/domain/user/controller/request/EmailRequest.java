package com.boardgo.domain.user.controller.request;

import jakarta.validation.constraints.Email;

public record EmailRequest(@Email String email) {}
