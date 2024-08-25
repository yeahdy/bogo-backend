package com.boardgo.init;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class UserInfoInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < 10; i++) {
            userRepository.save(
                    UserInfoEntity.builder()
                            .email("local" + i + "@aa.com")
                            .providerType(ProviderType.LOCAL)
                            .nickName("nickName" + i)
                            .password("password" + i)
                            .build());
        }
        for (int i = 10; i < 20; i++) {
            userRepository.save(
                    UserInfoEntity.builder()
                            .email("kakao" + i + "@aa.com")
                            .providerType(ProviderType.KAKAO)
                            .nickName("nickName" + i)
                            .password("password" + i)
                            .build());
        }
        for (int i = 20; i < 30; i++) {
            userRepository.save(
                    UserInfoEntity.builder()
                            .email("google" + i + "@aa.com")
                            .providerType(ProviderType.GOOGLE)
                            .nickName("nickName" + i)
                            .password("password" + i)
                            .build());
        }
    }
}
