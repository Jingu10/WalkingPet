package com.walkerholic.walkingpet.global.auth.controller;

import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.domain.users.entity.Users;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/close")
public class CloseController {

    @GetMapping
    @Operation(summary = "토큰 테스트", description = "통신 테스트")
    @ApiResponse(responseCode = "200", description = "S200 - 통신 테스트 성공", content = @Content(schema = @Schema(implementation = String.class)))
    public ResponseEntity<CommonResponseEntity> test(@AuthenticationPrincipal CustomUserDetail userDetail) {
        log.info("토큰 테스트 입장 성공");
        Integer userId = userDetail.getUsers().getUserId();
        System.out.println("user: " + userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, "토큰 테스트 입장 성공 userId: " + userId);
    }
}
