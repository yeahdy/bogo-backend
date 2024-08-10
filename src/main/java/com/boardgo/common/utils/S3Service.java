package com.boardgo.common.utils;

import static com.boardgo.common.exception.advice.dto.ErrorCode.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.boardgo.common.exception.CustomIllegalArgumentException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(String fileName, MultipartFile multipartFile) {
        String extensionFromFile = FileUtils.getExtensionFromFile(multipartFile);
        log.info("{}", fileName);
        return upload(fileName, multipartFile, extensionFromFile);
    }

    private String upload(String fileName, MultipartFile file, String extend) {
        String newFileName = fileName + extend;
        try {
            put(file, newFileName);
        } catch (IOException e) {
            throw new CustomIllegalArgumentException(NULL_ERROR.getCode(), "File이 Null입니다.");
        }
        return newFileName;
    }

    private void put(MultipartFile file, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata);
        amazonS3Client.putObject(putObjectRequest);
    }
}
