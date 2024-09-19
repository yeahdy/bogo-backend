package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.domain.user.controller.request.UserPersonalInfoUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserCommandUseCase {
    Long save(SignupRequest signupRequest);

    void updatePersonalInfo(Long userId, UserPersonalInfoUpdateRequest updateRequest);

    void updateProfileImage(Long userId, MultipartFile profileImage);
}
