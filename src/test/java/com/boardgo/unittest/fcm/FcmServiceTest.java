package com.boardgo.unittest.fcm;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.boardgo.common.exception.FcmException;
import com.boardgo.fcm.request.FcmMessageSendRequest;
import com.boardgo.fcm.service.FcmService;
import com.boardgo.fcm.service.FirebaseMessagingUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FcmServiceTest {
    @Mock private FirebaseMessagingUseCase firebaseMessagingUseCase;

    @InjectMocks private FcmService fcmService;

    @Test
    @DisplayName("토큰이 유효하지 않을 경우 fcm 메세지 전송에 실패한다")
    void 토큰이_유효하지_않을_경우_fcm_메세지_전송에_실패한다() {
        // given
        String failToken = "fksdfhsadkjh4gjlfjlsaag";
        FcmMessageSendRequest request =
                new FcmMessageSendRequest(failToken, "푸시 메세지 제목", "푸시 메세지 내용", null);
        given(firebaseMessagingUseCase.send(any())).willThrow(FcmException.class);
        // when
        // then
        assertThatThrownBy(() -> fcmService.sendFcmMessage(request))
                .isInstanceOf(FcmException.class);
    }
}
