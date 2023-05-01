package com.example.emotrak.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {
    @InjectMocks
    private FileUploadService fileUploadService;

    @Mock
    private AmazonS3 amazonS3;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private String bucketName = "test-bucket";
    private String target = "s3.amazonaws.com";
    private String replacement = "cloudfront.net";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileUploadService, "bucketName", bucketName);
        ReflectionTestUtils.setField(fileUploadService, "target", target);
        ReflectionTestUtils.setField(fileUploadService, "replacement", replacement);
    }

    @Nested
    @DisplayName("파일 업로드")
    class UploadFile {
        @Test
        @DisplayName("파일 업로드 성공 테스트")
        void uploadFile_success() {
            // given
            String originalFilename = "testFile.jpg";
            MockMultipartFile file = new MockMultipartFile("file", originalFilename, "image/jpg", "This is a test file".getBytes());

            when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);
            when(amazonS3.getUrl(eq(bucketName), anyString())).thenAnswer(invocation -> {
                String usedFilename = invocation.getArgument(1);
                return new URL("http://" + target + "/" + bucketName + "/" + usedFilename);
            });

            // when
            String uploadedUrl = fileUploadService.uploadFile(file);

            // then
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(bucketName), stringArgumentCaptor.capture());

            String generatedFilename = stringArgumentCaptor.getValue();
            assertTrue(generatedFilename.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}_testFile.jpg$"));

            assertEquals("http://" + replacement + "/" + bucketName + "/" + generatedFilename, uploadedUrl);
        }

        @Test
        @DisplayName("파일 업로드 실패 - IOException 테스트")
        void uploadFile_failure_ioException() {
            // given
            String originalFilename = "testFile.txt";
            MockMultipartFile file = new MockMultipartFile("file", originalFilename, "text/plain", "This is a test file".getBytes()) {
                @Override
                public InputStream getInputStream() throws IOException {
                    throw new IOException("Test IOException");
                }
            };
            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.uploadFile(file));

            // then
            assertEquals(CustomErrorCode.FILE_UPLOAD_ERROR, exception.getErrorCode());
            verify(amazonS3, never()).putObject(any(PutObjectRequest.class));
        }

        @Test
        @DisplayName("파일 업로드 실패 - AmazonServiceException 테스트")
        void uploadFile_failure_amazonServiceException() {
            // given
            String originalFilename = "testFile.txt";
            MockMultipartFile file = new MockMultipartFile("file", originalFilename, "text/plain", "This is a test file".getBytes());

            when(amazonS3.putObject(any(PutObjectRequest.class))).thenThrow(AmazonServiceException.class);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.uploadFile(file));

            // then
            assertEquals(CustomErrorCode.AWS_SERVICE_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        }

        @Test
        @DisplayName("파일 업로드 실패 - AmazonClientException 테스트")
        void uploadFile_failure_amazonClientException() {
            // given
            String originalFilename = "testFile.txt";
            MockMultipartFile file = new MockMultipartFile("file", originalFilename, "text/plain", "This is a test file".getBytes());

            when(amazonS3.putObject(any(PutObjectRequest.class))).thenThrow(AmazonClientException.class);

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.uploadFile(file));

            // then
            assertEquals(CustomErrorCode.AWS_CLIENT_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        }
    }

    @Nested
    @DisplayName("단일파일 삭제")
    class DeleteFile {
        @Test
        @DisplayName("파일 삭제 성공 테스트")
        void deleteFile_success() {
            // given
            String fileUrl = "http://" + replacement + "/" + bucketName + "/testFile.txt";
            String expectedFilename = "testFile.txt";

            // when
            fileUploadService.deleteFile(fileUrl);

            // then
            verify(amazonS3, times(1)).deleteObject(bucketName, expectedFilename);
        }

        @Test
        @DisplayName("파일 삭제 실패 - 기타 예외 테스트")
        void deleteFile_failure_otherException() {
            // given
            String fileUrl = "http://" + replacement + "/" + bucketName + "/testFile.txt";

            doThrow(RuntimeException.class).when(amazonS3).deleteObject(bucketName, "testFile.txt");

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.deleteFile(fileUrl));

            // then
            assertEquals(CustomErrorCode.FILE_DELETION_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).deleteObject(bucketName, "testFile.txt");
        }

        @Test
        @DisplayName("파일 삭제 실패 - AmazonServiceException 테스트")
        void deleteFile_failure_amazonServiceException() {
            // given
            String fileUrl = "http://" + replacement + "/" + bucketName + "/testFile.txt";

            doThrow(AmazonServiceException.class).when(amazonS3).deleteObject(bucketName, "testFile.txt");

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.deleteFile(fileUrl));

            // then
            assertEquals(CustomErrorCode.AWS_SERVICE_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).deleteObject(bucketName, "testFile.txt");
        }

        @Test
        @DisplayName("파일 삭제 실패 - AmazonClientException 테스트")
        void deleteFile_failure_amazonClientException() {
            // given
            String fileUrl = "http://" + replacement + "/" + bucketName + "/testFile.txt";

            doThrow(AmazonClientException.class).when(amazonS3).deleteObject(bucketName, "testFile.txt");

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.deleteFile(fileUrl));

            // then
            assertEquals(CustomErrorCode.AWS_CLIENT_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).deleteObject(bucketName, "testFile.txt");
        }

    }


    @Nested
    @DisplayName("다중파일 삭제")
    class DeleteFiles {
        @Test
        @DisplayName("다중 파일 삭제 성공 테스트")
        void deleteFiles_success() {
            // given
            List<String> fileUrls = Arrays.asList(
                    "http://" + replacement + "/" + bucketName + "/testFile1.txt",
                    "http://" + replacement + "/" + bucketName + "/testFile2.txt"
            );
            List<String> expectedFilenames = Arrays.asList("testFile1.txt", "testFile2.txt");

            // when
            fileUploadService.deleteFiles(fileUrls);

            // then
            ArgumentCaptor<DeleteObjectsRequest> argumentCaptor = ArgumentCaptor.forClass(DeleteObjectsRequest.class);
            verify(amazonS3, times(1)).deleteObjects(argumentCaptor.capture());
            DeleteObjectsRequest capturedRequest = argumentCaptor.getValue();

            assertEquals(bucketName, capturedRequest.getBucketName());
            List<DeleteObjectsRequest.KeyVersion> capturedKeys = capturedRequest.getKeys();
            assertEquals(expectedFilenames.size(), capturedKeys.size());

            for (int i = 0; i < expectedFilenames.size(); i++) {
                assertEquals(expectedFilenames.get(i), capturedKeys.get(i).getKey());
            }
        }
//        void deleteFiles_success() {
//            // given
//            List<String> fileUrls = Arrays.asList(
//                    "http://" + target + "/" + bucketName + "/file1.txt",
//                    "http://" + target + "/" + bucketName + "/file2.txt"
//            );
//
//            DeleteObjectsRequest expectedRequest = new DeleteObjectsRequest(bucketName)
//                    .withKeys("file1.txt", "file2.txt")
//                    .withQuiet(false);
//
//            when(amazonS3.deleteObjects(expectedRequest)).thenReturn(null);
//
//            // when
//            fileUploadService.deleteFiles(fileUrls);
//
//            // then
//            verify(amazonS3, times(1)).deleteObjects(expectedRequest);
//        }

        @Test
        @DisplayName("다중 파일 삭제 실패 - 기타 예외 테스트")
        void deleteFiles_failure_otherException() {
            // given
            List<String> fileUrls = Arrays.asList(
                    "http://" + replacement + "/" + bucketName + "/testFile1.txt",
                    "http://" + replacement + "/" + bucketName + "/testFile2.txt"
            );

            doThrow(RuntimeException.class).when(amazonS3).deleteObjects(any(DeleteObjectsRequest.class));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.deleteFiles(fileUrls));

            // then
            assertEquals(CustomErrorCode.FILE_DELETION_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
        }

        @Test
        @DisplayName("다중 파일 삭제 실패 - AmazonServiceException 테스트")
        void deleteFiles_failure_amazonServiceException() {
            // given
            List<String> fileUrls = Arrays.asList(
                    "http://" + replacement + "/" + bucketName + "/testFile1.txt",
                    "http://" + replacement + "/" + bucketName + "/testFile2.txt"
            );

            doThrow(AmazonServiceException.class).when(amazonS3).deleteObjects(any(DeleteObjectsRequest.class));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.deleteFiles(fileUrls));

            // then
            assertEquals(CustomErrorCode.AWS_SERVICE_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
        }

        @Test
        @DisplayName("다중 파일 삭제 실패 - AmazonClientException 테스트")
        void deleteFiles_failure_amazonClientException() {
            // given
            List<String> fileUrls = Arrays.asList(
                    "http://" + replacement + "/" + bucketName + "/testFile1.txt",
                    "http://" + replacement + "/" + bucketName + "/testFile2.txt"
            );

            doThrow(AmazonClientException.class).when(amazonS3).deleteObjects(any(DeleteObjectsRequest.class));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> fileUploadService.deleteFiles(fileUrls));

            // then
            assertEquals(CustomErrorCode.AWS_CLIENT_ERROR, exception.getErrorCode());
            verify(amazonS3, times(1)).deleteObjects(any(DeleteObjectsRequest.class));
        }

    }



}



