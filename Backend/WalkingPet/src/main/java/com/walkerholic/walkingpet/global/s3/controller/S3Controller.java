package com.walkerholic.walkingpet.global.s3.controller;

import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.global.error.GlobalErrorCode;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import com.walkerholic.walkingpet.global.s3.dto.response.S3FileUpload;
import com.walkerholic.walkingpet.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/s3/test")
public class S3Controller {
    private final S3Service s3Service;

    //1. s3에 사진 업로드 테스트
    @PostMapping("/upload")
    @Operation(summary = "s3 업로드 테스트", description = "s3 연결을 위한 테스트")
    @ApiResponse(responseCode = "200", description = "S200 - 테스트 사진 업로드 성공")
    public ResponseEntity<CommonResponseEntity> uploadImageFile(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("image") MultipartFile multipartFile) {
        S3FileUpload s3FileUpload = null;

        if(multipartFile != null){ // 파일 업로드한 경우에만
            try{// 파일 업로드
                s3FileUpload = s3Service.saveFile(multipartFile); // S3 버킷의 images 디렉토리 안에 저장됨
                System.out.println("파일 업로드 성공");
            }catch (IOException e){
                System.out.println("파일이 존재하지 않습니다.");
            }
        }
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, s3FileUpload);
    }

    //2. s3에 사진 제거 테스트
    @PostMapping("/delete")
    @Operation(summary = "s3 데이터 제거 테스트", description = "s3 데이터 제거를 위한 테스트")
    @ApiResponse(responseCode = "200", description = "S200 - 테스트 사진 제거 성공")
    public ResponseEntity<CommonResponseEntity> deleteImageFile(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("fileName") String fileName) {
        s3Service.deleteImage(fileName);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, true);
    }
}
