package com.boardgo.integration.user.service;

import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static com.boardgo.integration.fixture.UserPrTagFixture.userPrTagEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserPrTagCommandUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserPrTagCommandServiceV1Test extends IntegrationTestSupport {

    @Autowired private UserPrTagCommandUseCase userPrTagCommandUseCase;
    @Autowired private UserPrTagRepository userPrTagRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("전달된 PR태그가 없을 경우 모두 삭제된다")
    void 전달된_PR태그가_없을_경우_모두_삭제된다() {
        // given
        UserInfoEntity userInfo = userRepository.save(localUserInfoEntity());
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "123"));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "456"));

        // when
        userPrTagCommandUseCase.updatePrTags(null, userInfo.getId());

        // then
        List<UserPrTagEntity> trTags = userPrTagRepository.findByUserInfoId(userInfo.getId());
        assertThat(trTags).isEmpty();
    }
}
