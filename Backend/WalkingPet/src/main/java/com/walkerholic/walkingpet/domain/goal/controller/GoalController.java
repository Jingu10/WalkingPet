package com.walkerholic.walkingpet.domain.goal.controller;

import com.walkerholic.walkingpet.domain.goal.dto.response.GoalRewardDTO;
import com.walkerholic.walkingpet.domain.goal.dto.response.UserGoalInfoDTO;
import com.walkerholic.walkingpet.domain.goal.service.GoalService;
import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    @GetMapping("/info")
    @Operation(summary = "유저의 목표 정보 출력", description = "정수로 저장된 유저의 일일 목표를 true, false 배열로 출력")
    public ResponseEntity<CommonResponseEntity> getGoalInfo(@AuthenticationPrincipal CustomUserDetail userDetail){
        Integer userId = userDetail.getUsers().getUserId();
        log.info("유저의 목표 정보 출력 GaolController userGoalInfo - userId: {}", userId);
        UserGoalInfoDTO userGoalInfoDTO = goalService.getUserGoalInfo(userId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userGoalInfoDTO);
    }

    @GetMapping("/reward")
    @Operation(summary = "유저 목표 달성 보상 확인", description = "특정 걸음수를 달성했을시 목표 정보를 업데이트")
    public ResponseEntity<CommonResponseEntity> getGoalReward(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam(name = "goalStep")int goalStep){
        Integer userId = userDetail.getUsers().getUserId();
        GoalRewardDTO userGoalInfoDTO = goalService.getGoalReward(userId, goalStep);
        log.info("특정 걸음수를 달성했을 때 보상 정보 저장 GaolController userGoalInfo - userId: {}", userId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userGoalInfoDTO);
    }

}
