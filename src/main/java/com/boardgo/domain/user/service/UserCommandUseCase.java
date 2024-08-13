package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.controller.dto.SocialSignupRequest;
import com.boardgo.domain.user.controller.dto.UserPersonalInfoUpdateRequest;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UserCommandUseCase {
    Long signup(SignupRequest signupRequest);

    Long socialSignup(SocialSignupRequest socialSignupRequest, Long userId);

    void updatePersonalInfo(Long userId, UserPersonalInfoUpdateRequest updateRequest);

    void updateProfileImage(Long userId, MultipartFile profileImage);

    void updatePrTags(List<String> changedPrTag, Long userId);
}
