package com.walkerholic.walkingpet.domain.character.controller;

import com.walkerholic.walkingpet.domain.character.dto.request.ChangeUserCharacterIdRequest;
import com.walkerholic.walkingpet.domain.character.dto.request.ResetInitStatusRequest;
import com.walkerholic.walkingpet.domain.character.dto.response.ResetStatResponse;
import com.walkerholic.walkingpet.domain.character.dto.response.UserCharacterInfoResponse;
import com.walkerholic.walkingpet.domain.character.dto.response.UserCharacterStatResponse;
import com.walkerholic.walkingpet.domain.character.dto.response.UserStepResponse;
import com.walkerholic.walkingpet.domain.character.service.UserCharacterService;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/character")
public class CharacterController {

    private final UserCharacterService userCharacterService;

    @GetMapping
    @Operation(summary = "캐릭터 정보 확인", description = "유저의 userCharacterId로  캐릭터 정보 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 해당 캐릭터를 찾기 성공", content = @Content(schema = @Schema(implementation = UserCharacterInfoResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 유저의 해당 캐릭터를 찾기 실패")
    public ResponseEntity<CommonResponseEntity> getUserCharacterInfo(@RequestParam("userCharacterId") int userCharacterId) {
        log.info("CharacterController getUserCharacterInfo - userCharacterId: {}", userCharacterId);

        UserCharacterInfoResponse userCharacterInfo = userCharacterService.getUserCharacterInfo(userCharacterId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userCharacterInfo);
    }

    // /api/character/stat?value=power&userCharacterId=3
    @GetMapping("/stat")
    @Operation(summary = "스탯 분배", description = "유저의 캐릭터 스탯을 분배")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 캐릭터 스탯 분배 성공", content = @Content(schema = @Schema(implementation = UserCharacterStatResponse.class)))
    @ApiResponse(responseCode = "403", description = "C400 - 유저의 해당 캐릭터 스탯 포인트가 부족")
    @ApiResponse(responseCode = "404", description = "C400 - 유저의 해당 캐릭터를 찾기 실패")
    public ResponseEntity<CommonResponseEntity> statDistribution(@RequestParam("userCharacterId") int userCharacterId, @RequestParam("value") String value) {
        log.info("CharacterController statDistribution - userCharacterId: {}, value: {}", userCharacterId, value);

        UserCharacterStatResponse userCharacterStatInfo = userCharacterService.addStatPoint(userCharacterId, value);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userCharacterStatInfo);
    }

    @PostMapping("/stat/reset")
    @Operation(summary = "스탯 분배 초기화", description = "유저의 캐릭터 스탯을 분배 초기화")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 캐릭터 스탯 분배 초기화 성공", content = @Content(schema = @Schema(implementation = ResetStatResponse.class)))
    @ApiResponse(responseCode = "400", description = "C400 - 이미 스탯 초기화 버튼 누름")
    public ResponseEntity<CommonResponseEntity> resetStatDistribution(@RequestBody ResetInitStatusRequest resetInitStatusRequest) {
        log.info("CharacterController resetStatDistribution - userCharacterId: {}", resetInitStatusRequest);

        ResetStatResponse resetStatResponse = userCharacterService.resetInitStatus(resetInitStatusRequest);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, resetStatResponse);
    }

    @PostMapping("/change")
    @Operation(summary = "캐릭터 변경", description = "유저의 현재 캐릭터 변경")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 캐릭터 변경 성공")
    public ResponseEntity<CommonResponseEntity> changeUserCharacter(@RequestBody ChangeUserCharacterIdRequest changeUserCharacterIdRequest) {
        int userId = 1;
        log.info("CharacterController changeUserCharacter - userId: {}, userCharacterId: {}", userId, changeUserCharacterIdRequest.getUserCharacterId());
        userCharacterService.changeUserCharacter(userId, changeUserCharacterIdRequest);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/checkstep")
    @Operation(summary = "사용자의 걸음수 측정", description = "앱 시작시 걸음수 측정")
    @ApiResponse(responseCode = "200", description = "S200 - 걸음수 측정 성공")
    public ResponseEntity<CommonResponseEntity> getUserStep() {
        int userId = 1;
        int frontStep = 100;
        log.info("CharacterController getUserStep - userId: {}, step: {}", userId, frontStep);

        UserStepResponse userStepResponse = userCharacterService.checkUserStep(userId, frontStep);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userStepResponse);
    }



    @GetMapping("/test")
    @Operation(summary = "통신 테스트", description = "통신 테스트")
    @ApiResponse(responseCode = "200", description = "S200 - 통신 테스트 성공", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<CommonResponseEntity> test() {
        log.info("통신 테스트");
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, "통신 테스트");
    }

}
