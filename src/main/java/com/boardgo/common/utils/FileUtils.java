package com.boardgo.common.utils;

import static com.boardgo.common.exception.advice.dto.ErrorCode.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

    public static String getExtensionFromFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            throw new CustomIllegalArgumentException(NULL_ERROR.getCode(), "파일이 유효하지 않습니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        int lastDotIndex = originalFilename.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            throw new CustomIllegalArgumentException(NULL_ERROR.getCode(), "파일에 확장자가 없습니다.");
        }

        return originalFilename.substring(lastDotIndex);
    }

    public static String getUniqueFileName(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            throw new IllegalArgumentException("파일이 유효하지 않습니다.");
        }
        return UUID.randomUUID().toString();
    }
}
