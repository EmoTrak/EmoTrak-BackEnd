package com.example.emotrak.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FileUploadConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;


    // 외부 의존성을 Bean 으로 등록해서 DI를 통해 주입할 수 있도록 함
    @Bean
    public AmazonS3 amazonS3Client() {
        // accessKey 와 secretKey, region 를 이용하여 자격증명 객체를 획득
        AWSCredentials awsCreds = new BasicAWSCredentials(this.accessKey, this.secretKey);
        // AmazonS3Client 가 deprecated 됨에 따라, AmazonS3ClientBuilder 를 사용(AmazonS3 인터페이스)
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }
}
