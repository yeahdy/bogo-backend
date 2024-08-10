package com.boardgo.unittest.utils;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.common.utils.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class FileUtilsTest {

    @Test
    @DisplayName("확장자만 가져올 수 있다")
    void 확장자만_가져올_수_있다() {
        // given
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile(
                        "file",
                        "test.png",
                        "image/png",
                        "This is a test image file content".getBytes());
        // when
        String extensionFromFile = FileUtils.getExtensionFromFile(mockMultipartFile);
        // then
        assertThat(extensionFromFile).isEqualTo(".png");
    }
}
