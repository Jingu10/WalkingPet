package com.walkerholic.walkingpet.domain.gacha.controller;

import com.walkerholic.walkingpet.domain.gacha.dto.response.GachaCountResponse;
import com.walkerholic.walkingpet.domain.gacha.dto.response.GachaResultResponse;
import com.walkerholic.walkingpet.domain.gacha.service.GachaService;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamUsersResponse;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/gacha")
public class GachaController {

    private final GachaService gachaService;

    @Operation(summary = "뽑기 가능 횟수 조회", description = "뽑기 가능 횟수 조회")
    @ApiResponse(responseCode = "200", description = "S200 - 뽑기 가능 횟수 조회 성공", content = @Content(schema = @Schema(implementation = GachaCountResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 뽑기 가능 횟수 조회 실패")
    @GetMapping("/count")
    public ResponseEntity<CommonResponseEntity> getGachaCount(@AuthenticationPrincipal CustomUserDetail userDetail){
        Integer userId = userDetail.getUsers().getUserId();
        GachaCountResponse gachaCount = gachaService.getGachaCount(userId);
        log.info("뽑기 가능 횟수 조회 getGachaCount - userId: {}", userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, gachaCount);
    }

    @Operation(summary = "뽑기 결과 조회", description = "선택한 상자에 따라 뽑기 결과를 조회한다")
    @ApiResponse(responseCode = "200", description = "S200 - 뽑기 결과 조회 성공", content = @Content(schema = @Schema(implementation = GachaResultResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - 뽑기 결과 조회 실패")
    @GetMapping("/result")
    public ResponseEntity<CommonResponseEntity> getGachaResult(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("box") String boxType){
        Integer userId = userDetail.getUsers().getUserId();
        GachaResultResponse gachaResult = gachaService.getGachaResult(boxType, userId);
        log.info("뽑기 결과 조회 getGachaResult - boxType: {}, userId: {}", boxType, userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS,gachaResult);
    }
}
