package com.boardgo.common.utils;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.exception.CustomS3Exception;
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

    public String upload(String folderName, String fileName, MultipartFile multipartFile) {
        String extensionFromFile = FileUtils.getExtensionFromFile(multipartFile);
        log.info("{}", fileName);
        return upload(folderName, fileName, multipartFile, extensionFromFile);
    }

    private String upload(String folderName, String fileName, MultipartFile file, String extend) {
        String newFileName = folderName + "/" + fileName + extend;
        try {
            put(file, newFileName);
        } catch (IOException e) {
            throw new CustomNullPointException("File이 Null입니다.");
        } catch (SdkClientException sce) {
            throw new CustomS3Exception(sce.getMessage());
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

    public void deleteFile(String fileName) {
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (SdkClientException sce) {
            throw new CustomS3Exception(sce.getMessage());
        }
    }
}
