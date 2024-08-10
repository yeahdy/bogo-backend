package com.boardgo.oauth2.service;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.oauth2.dto.OAuth2CreateUserRequest;
import com.boardgo.oauth2.dto.OAuth2Response;
import com.boardgo.oauth2.dto.OAuth2UserResponseFactory;
import com.boardgo.oauth2.entity.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserInfoMapper userInfoMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return this.process(userRequest, oAuth2User);
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        ProviderType providerType =
                ProviderType.valueOf(
                        userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2Response oAuth2Response =
                OAuth2UserResponseFactory.getOAuth2Response(
                        providerType, oAuth2User.getAttributes());

        UserInfoEntity userInfoEntity =
                userRepository
                        .findByEmailAndProviderType(oAuth2Response.getProviderId(), providerType)
                        .orElseGet(
                                () ->
                                        createUser(
                                                new OAuth2CreateUserRequest(
                                                        oAuth2Response.getProviderId(),
                                                        providerType)));

        return CustomOAuth2User.create(userInfoEntity, oAuth2User.getAttributes());
    }

    private UserInfoEntity createUser(OAuth2CreateUserRequest auth2CreateUserRequest) {
        UserInfoEntity userInfoEntity = userInfoMapper.toUserInfoEntity(auth2CreateUserRequest);
        return userRepository.save(userInfoEntity);
    }
}
