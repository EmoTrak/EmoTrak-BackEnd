package com.example.emotrak.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.emotrak.exception.CustomErrorCode;
import com.example.emotrak.exception.CustomException;
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

    //AWS SDK 를 사용하여 AWS S3에 파일을 업로드
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
            // 출력 관련 예외로, 파일이나 네트워크와 같은 입출력 작업 중에 발생할 수 있는 예외
        } catch (IOException e) {
            throw new CustomException(CustomErrorCode.FILE_UPLOAD_ERROR);
            //  S3 서비스에 요청을 보내고 응답을 받는 과정에서 문제가 발생할 경우
        } catch (AmazonServiceException e) {
            throw new CustomException(CustomErrorCode.AWS_SERVICE_ERROR);
            // 네트워크 연결 문제나 인증 문제 등이 있을 경우
        } catch (AmazonClientException e) {
            throw new CustomException(CustomErrorCode.AWS_CLIENT_ERROR);
        }
    }

    // 원래 파일 이름을 랜덤 UUID 와 조합하여 새로운 파일 이름을 생성
    private String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    //s3파일수정(파일삭제 후 새 파일 업로드)
    public String updateFile(String oldFileUrl, MultipartFile newFile) {
            // 이미지가 null이 아닌 경우에만 S3에서 이미지 파일 삭제
            if (oldFileUrl != null && !oldFileUrl.isEmpty()) {
                deleteFile(oldFileUrl);
            }
            // 새 파일 업로드
            return uploadFile(newFile);
    }

    //s3파일삭제
    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            amazonS3.deleteObject(bucketName, fileName);
        } catch (AmazonServiceException e) {
            // 권한이 없거나, S3 버킷이 없는 경우, S3 서비스가 다운되거나 요청이 제한되는 경우
            throw new CustomException(CustomErrorCode.AWS_SERVICE_ERROR);
        } catch (AmazonClientException e) {
            // 인터넷 연결 문제, 잘못된 클라이언트 설정, 잘못된 AWS 자격 증명
            throw new CustomException(CustomErrorCode.AWS_CLIENT_ERROR);
        } catch (Exception e) {
            /*
             * 위에서 언급한 예외들 외에 발생할 수 있는 다른 모든 예외를 처리
             * 파일 URL 에서 파일 이름을 추출하는 과정에서 발생할 수 있는 예외,
             * 예를 들어, fileUrl 이 null 이거나 올바르지 않은 형식일 때 발생할 수 있는 StringIndexOutOfBoundsException 등이 포함
             */
            throw new CustomException(CustomErrorCode.FILE_DELETION_ERROR);

        }
    }
}

