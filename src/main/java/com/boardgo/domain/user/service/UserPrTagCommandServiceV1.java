package com.boardgo.domain.user.service;

import static com.boardgo.common.utils.ValidateUtils.validatePrTag;

import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPrTagCommandServiceV1 implements UserPrTagCommandUseCase {

    private final UserPrTagRepository userPrTagRepository;

    @Override
    public void bulkInsertPrTags(List<String> prTags, Long userInfoId) {
        userPrTagRepository.bulkInsertPrTags(prTags, userInfoId);
    }

    @Override
    public void updatePrTags(List<String> changedPrTag, Long userId) {
        List<UserPrTagEntity> prTags = userPrTagRepository.findByUserInfoId(userId);
        if (!Objects.isNull(changedPrTag)) {
            validatePrTag(changedPrTag);
        }
        userPrTagRepository.deleteAllInBatch(prTags);
        userPrTagRepository.bulkInsertPrTags(changedPrTag, userId);
    }
}
