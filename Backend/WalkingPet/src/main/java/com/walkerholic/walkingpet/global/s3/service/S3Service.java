package com.walkerholic.walkingpet.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.walkerholic.walkingpet.global.s3.dto.response.S3FileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //파일 올리기
    public S3FileUpload saveFile(MultipartFile multipartFile) throws IOException {
        //파일 이름 만들기
        String fileName = generateFileName(multipartFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);
        System.out.println(amazonS3.getUrl(bucket, fileName));

        return S3FileUpload.builder()
                .imageUrl(amazonS3.getUrl(bucket, fileName).toString())
                .imageFileName(fileName)
                .build();
    }

    //파일 삭제
    public void deleteImage(String fileName)  {
        amazonS3.deleteObject(bucket, fileName);
    }

    // 파일 이름 생성 메소드
    public String generateFileName(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return "walkingpet"+UUID.randomUUID().toString() + fileExtension;
    }
}
