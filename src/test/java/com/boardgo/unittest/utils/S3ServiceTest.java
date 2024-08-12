package com.boardgo.unittest.utils;

import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.boardgo.common.utils.S3Service;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock private AmazonS3 amazonS3Client;

    @InjectMocks private S3Service s3Service;

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(amazonS3Client);
    }

    @Test
    void testUpload() {
        // given
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile(
                        "file",
                        "test.png",
                        "image/png",
                        "This is a test image file content".getBytes());

        String fileName = UUID.randomUUID().toString();

        // Mock the necessary AmazonS3 methods
        PutObjectResult putObjectResult = new PutObjectResult();
        when(amazonS3Client.putObject(any(PutObjectRequest.class))).thenReturn(putObjectResult);

        // when
        String uploadedFileName = s3Service.upload("meeting", fileName, mockMultipartFile);

        // then
        verify(amazonS3Client).putObject(any(PutObjectRequest.class));
        Assertions.assertThat(uploadedFileName).isEqualTo("meeting/" + fileName + ".png");
    }
}
