package com.walkerholic.walkingpet.domain.record.controller;

import com.walkerholic.walkingpet.domain.record.dto.response.*;
import com.walkerholic.walkingpet.domain.record.service.RecordService;
import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    //1. 기록 올리기
    @PostMapping("/upload")
    @Operation(summary = "기록 업로드하기", description = "유저가 찍은 이미지와 유저의 정보를 저장")
    @ApiResponse(responseCode = "200", description = "S200 - 업로드 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "R400 - 업로드 실패")
    public ResponseEntity<CommonResponseEntity> uploadRecord(@AuthenticationPrincipal CustomUserDetail userDetail,
                                                             @RequestParam("image") MultipartFile multipartFile,
                                                             @RequestParam("characterId") int characterId,
                                                             @RequestParam("latitude") double latitude,
                                                             @RequestParam("longitude") double longitude) {
        int userId = userDetail.getUsers().getUserId();
        log.info("유저의 기록 등록하기 - userId: {}", userId);

        UploadRecordResponse uploadRecordResponse = recordService.uploadRecord(userId, multipartFile, characterId, latitude, longitude);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, uploadRecordResponse);
    }

    //2. 유저가 올린 모든 사진 가져오기(유저 id로 저장되어 있는 모든 사진 가져오기)
    @GetMapping("/my-record")
    @Operation(summary = "유저의 모든 기록 가져오기", description = "유저가 작성하고 업로드한 모든 기록을 가져옵니다.")
    @ApiResponse(responseCode = "200", description = "S200 - 가입한 회원인지 확인 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "R400 - 유저가 작성한 기록 없음")
    public ResponseEntity<CommonResponseEntity> getAllRecord(@AuthenticationPrincipal CustomUserDetail userDetail) {
        int userId = userDetail.getUsers().getUserId();
        log.info("유저가 등록한 모든 기록 가져오기 - userId: {}", userId);
        AllUserRecordResponse allUserRecordResponse = recordService.getAllRecord(userId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, allUserRecordResponse);
    }

    //3. 기록 삭제하기
    @PostMapping("/delete")
    @Operation(summary = "기록 삭제", description = "유저가 등록한 기록 삭제")
    @ApiResponse(responseCode = "200", description = "S200 - 기록 삭제 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "R400 - 기록 삭제 실패")
    public ResponseEntity<CommonResponseEntity> deleteRecord(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("fileName") String fileName){
        int userId = userDetail.getUsers().getUserId();
        log.info("유저가 등록한 기록 삭제하기 - userId: {}", userId);
        Boolean canDelete = recordService.deleteImage(userId, fileName);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, canDelete);
    }

    //4. 내 주변에 있는 유저의 기록 목록 가져오기
    /**
     *  맨 처음 로드할때 행정구역을 기준으로 로드 하게끔
     */
    @GetMapping("/normal")
    @Operation(summary = "일반 기록을 로드하는 api", description = "일반 기록 로드하기")
    @ApiResponse(responseCode = "200", description = "S200 - 기록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "R400 - 기록 조회 실패")
    public ResponseEntity<CommonResponseEntity> loadNormalRecordByCity(@AuthenticationPrincipal CustomUserDetail userDetail){
        int userId = userDetail.getUsers().getUserId();
        log.info("선택한 기록이 가까운지 멀리 있는지 확인 - userId: {}", userId);
        NormalRecordResponse normalRecordResponse = recordService.loadNormalRecord(userId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, normalRecordResponse);
    }

    //5. 선택한 기록이 가까운지 멀리 있는지 확인하는 api
    @GetMapping("/check-record")
    @Operation(summary = "선택한 기록이 가까운지 멀리 있는지 확인하는 api", description = "다른 유저의 기록 확인하기")
    @ApiResponse(responseCode = "200", description = "S200 - 기록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "R400 - 기록 조회 실패")
    public ResponseEntity<CommonResponseEntity> checkAnotherUserRecord(@AuthenticationPrincipal CustomUserDetail userDetail,
                                                                       @RequestParam("latitude")double latitude,
                                                                       @RequestParam("longitude")double longitude,
                                                                       @RequestParam("recordId")int recordId){
        int userId = userDetail.getUsers().getUserId();
        log.info("선택한 기록이 가까운지 멀리 있는지 확인 - userId: {}", userId);
        CheckCloseRecordResponse checkAnotherUserRecord = recordService.checkAnotherUserRecord(userId, latitude, longitude, recordId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, checkAnotherUserRecord);
    }

    //6. 이벤트성 기록을 로드하는 api(전체)
    /**
     *  맨 처음 로드할때 행정구역을 기준으로 로드 하게끔
     */
    @GetMapping("/event/all")
    @Operation(summary = "이벤트 기록을 로드하는 api", description = "이벤트 기록 로드하기")
    @ApiResponse(responseCode = "200", description = "S200 - 기록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "R400 - 기록 조회 실패")
    public ResponseEntity<CommonResponseEntity> loadEventRecord(@AuthenticationPrincipal CustomUserDetail userDetail){
        int userId = userDetail.getUsers().getUserId();
        log.info("선택한 기록이 가까운지 멀리 있는지 확인 - userId: {}", userId);
        EventRecordResponse eventRecordResponse = recordService.loadEventRecord(userId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, eventRecordResponse);
    }

    //7. 이벤트성 기록을 로드하는 api(도시 기준)
    /**
     *  맨 처음 로드할때 행정구역을 기준으로 로드 하게끔
     */
    @GetMapping("/event/city")
    @Operation(summary = "이벤트 기록을 로드하는 api", description = "이벤트 기록 로드하기")
    @ApiResponse(responseCode = "200", description = "S200 - 기록 조회 성공", content = @Content(schema = @Schema(implementation = CommonResponseEntity.class)))
    @ApiResponse(responseCode = "404", description = "R400 - 기록 조회 실패")
    public ResponseEntity<CommonResponseEntity> loadEventRecordByCity(@AuthenticationPrincipal CustomUserDetail userDetail,
                                                                @RequestParam("latitude")double latitude,
                                                                @RequestParam("longitude")double longitude){
        int userId = userDetail.getUsers().getUserId();
        log.info("선택한 기록이 가까운지 멀리 있는지 확인 - userId: {}", userId);
        EventRecordResponse eventRecordResponse = recordService.loadEventRecordByCity(userId, latitude, longitude);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, eventRecordResponse);
    }

    //8. 이벤트 기록 등록하는 api
    @PostMapping("/event/regist")
    public ResponseEntity<CommonResponseEntity> uploadEventRecord(@AuthenticationPrincipal CustomUserDetail userDetail,
                                                             @RequestParam("image") MultipartFile multipartFile,
                                                             @RequestParam("characterId") int characterId,
                                                             @RequestParam("latitude") double latitude,
                                                             @RequestParam("longitude") double longitude,
                                                             @RequestParam("content") String content) {
        int userId = userDetail.getUsers().getUserId();
        log.info("이벤트 기록 등록하기 - userId: {}", userId);

        UploadRecordResponse uploadRecordResponse = recordService.uploadEventRecord(userId, multipartFile, characterId, latitude, longitude, content);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, uploadRecordResponse);
    }
}
