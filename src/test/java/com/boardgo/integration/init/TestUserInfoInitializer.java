package com.boardgo.integration.init;

import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestUserInfoInitializer {

    private final UserRepository userRepository;

    public TestUserInfoInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void generateUserData() {
        for (int i = 0; i < 10; i++) {
            userRepository.save(
                    UserInfoEntity.builder()
                            .email("local" + i + "@aa.com")
                            .providerType(ProviderType.LOCAL)
                            .nickName("nickName" + i)
                            .password("password" + i)
                            .profileImage("profileImage" + i)
                            .build());
        }
        for (int i = 10; i < 20; i++) {
            userRepository.save(
                    UserInfoEntity.builder()
                            .email("kakao" + i + "@aa.com")
                            .providerType(ProviderType.KAKAO)
                            .nickName("nickName" + i)
                            .password("password" + i)
                            .profileImage("profileImage" + i)
                            .build());
        }
        for (int i = 20; i < 30; i++) {
            userRepository.save(
                    UserInfoEntity.builder()
                            .email("google" + i + "@aa.com")
                            .providerType(ProviderType.GOOGLE)
                            .nickName("nickName" + i)
                            .password("password" + i)
                            .profileImage("profileImage" + i)
                            .build());
        }
    }
}
