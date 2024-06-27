package com.walkerholic.walkingpet.domain.fcmtoken.controller;

import com.walkerholic.walkingpet.domain.fcmtoken.dto.request.SaveFcmTokenRequest;
import com.walkerholic.walkingpet.domain.fcmtoken.service.FcmTokenService;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamResponse;
import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/fcm")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;
    @Operation(summary = "FCM 토큰 저장", description = "FCM 토큰을 서버에 저장하기")
    @ApiResponse(responseCode = "200", description = "S200 - FCM 토큰 저장 성공", content = @Content(schema = @Schema(implementation = TeamResponse.class)))
    @ApiResponse(responseCode = "404", description = "C400 - FCM 토큰 저장 실패")
    @PostMapping("/saveToken")
    public void saveFcmToken(@AuthenticationPrincipal CustomUserDetail userDetail,@RequestBody SaveFcmTokenRequest saveFcmTokenRequest) {
        Integer userId = userDetail.getUsers().getUserId();
        String token = saveFcmTokenRequest.getToken();
        fcmTokenService.saveToken(userId,token);
        log.info("FCM 토큰 저장 saveToken - userId:{}, token:{}", userId,token);
    }
}
