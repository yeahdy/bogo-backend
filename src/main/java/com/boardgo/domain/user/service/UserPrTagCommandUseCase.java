package com.boardgo.domain.user.service;

import java.util.List;

public interface UserPrTagCommandUseCase {
    void bulkInsertPrTags(List<String> prTags, Long userInfoId);

    void updatePrTags(List<String> changedPrTag, Long userId);
}
