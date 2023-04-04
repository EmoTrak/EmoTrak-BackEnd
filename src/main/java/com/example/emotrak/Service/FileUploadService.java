package com.example.emotrak.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

//AWS S3를 사용하여 파일 업로드, 수정, 삭제를 수행하는 Service Class
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    //AWS SDK를 사용하여 AWS S3에 파일을 업로드
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = generateFileName(file.getOriginalFilename());
            //ObjectMetadata 객체를 생성하여 업로드된 파일의 메타데이터(파일 크기 및 콘텐츠 유형)를 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());
            //AmazonS3 객체를 사용, putObject 메서드를 호출하여 파일을 업로드
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream(), objectMetadata);
            amazonS3.putObject(putObjectRequest);
            // 업로드된 파일의 URL을 문자열로 반환
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            throw new S3FileUploadException("File upload to S3 failed", e);
        }
    }

    // 원래 파일 이름을 랜덤 UUID와 조합하여 새로운 파일 이름을 생성
    private String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    //s3파일수정(파일삭제 후 새 파일 업로드)
    public String updateFile(String oldFileUrl, MultipartFile newFile) {
        try {
            // 이전 파일 삭제
            deleteFile(oldFileUrl);
            // 새 파일 업로드
            return uploadFile(newFile);
        } catch (S3FileUploadException e) {
            throw new S3FileUploadException("File update in S3 failed", e);
        }
    }

    //s3파일삭제
    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            amazonS3.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            throw new S3FileUploadException("File deletion from S3 failed", e);
        }
    }


    // 예외 처리를 위한 사용자 정의 예외를 추가합니다.
    public class S3FileUploadException extends RuntimeException {
        public S3FileUploadException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}

