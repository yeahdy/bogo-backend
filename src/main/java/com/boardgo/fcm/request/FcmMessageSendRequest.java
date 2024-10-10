package com.boardgo.fcm.request;

public record FcmMessageSendRequest(String token, String title, String content, String pathUrl) {}
