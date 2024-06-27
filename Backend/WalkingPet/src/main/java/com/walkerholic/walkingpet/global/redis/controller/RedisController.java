package com.walkerholic.walkingpet.global.redis.controller;

import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingAndUserInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.ReailtimeStepRankingInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.YesterdayStepRankingInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.request.RealtimeStepRequest;
import com.walkerholic.walkingpet.domain.ranking.dto.response.RedisStepRankingResponse;
import com.walkerholic.walkingpet.domain.ranking.dto.response.StepRankingResponse;
import com.walkerholic.walkingpet.domain.ranking.service.RankingService;
import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import com.walkerholic.walkingpet.global.redis.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/redis")
public class RedisController {
    private final RankingService rankingService;
    private final AccStepRankingRedisService accStepRankingRedisService;
    private final YesterdayStepRankingRedisService yesterdayStepRankingRedisService;
    private final RealtimeStepRankingRedisService realtimeStepRankingRedisService;
    private final RankingRedisService rankingRedisService;
    private final UserInfoRedisService userInfoRedisService;

    @GetMapping("/saveTest")
    @Operation(summary = "redis 모든 랭킹 저장 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> saveRedisAllRankingInfo() {
        log.info("redis 모든 랭킹 저장 테스트 - redis test saveRedisAccRankingInfo");

        rankingRedisService.saveRedisUserAndAllRanking();

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/saveDailyStep")
    @Operation(summary = "redis의 모든 걸음수 저장 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> saveRedisAllDailyStep() {
        log.info("redis의 모든 걸음수 저장 테스트 - redis test saveRedisAllDailyStep");

        realtimeStepRankingRedisService.saveUserDailyStep();

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/saveTest/accStepRanking")
    @Operation(summary = "redis 누적 랭킹 저장 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> saveRedisAccRankingInfo() {
        log.info("redis 누적 랭킹 저장 테스트 - redis test saveRedisAccRankingInfo");

        List<AccStepRankingAndUserInfo> userAccStepList = rankingService.getUserAccStepAndInfoList();

        for (AccStepRankingAndUserInfo stepInfo : userAccStepList) {
            accStepRankingRedisService.saveAccStepList(stepInfo);
        }

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/saveTest/yesterdayStepRanking")
    @Operation(summary = "redis 어제 걸음수 랭킹 저장 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> saveRedisYesterdayRankingInfo() {
        log.info("redis 어제 걸음수 랭킹 저장 테스트 - redis test saveRedisYesterdayRankingInfo");

        List<YesterdayStepRankingInfo> userYseterdayStepList = rankingService.getUserYseterdayStepList();

        for (YesterdayStepRankingInfo stepInfo : userYseterdayStepList) {
            yesterdayStepRankingRedisService.saveYesterdayStepList(stepInfo);
        }

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/saveTest/allUserRealtimeStep")
    @Operation(summary = "redis 모든 사용자 실시간 걸음수 저장 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> saveRedisAllUserRealtimeRankingInfo() {
        log.info("redis 어제 걸음수 랭킹 저장 테스트 - redis test saveRedisYesterdayRankingInfo");

        List<ReailtimeStepRankingInfo> userRealtimeStepList = rankingService.getUserRealtimeStepList();

        for (ReailtimeStepRankingInfo stepInfo : userRealtimeStepList) {
            realtimeStepRankingRedisService.saveAllUserDailyStepList(stepInfo);
        }

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/saveTest/userRealtimeStep")
    @Operation(summary = "redis 특정 사용자 실시간 걸음수 저장 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> saveRedisRealtimeStep() {
        int userId = 1;
        int step = 1114;
        log.info("redis 실시간 걸음수 저장 테스트 - redis test saveRedisRealtimeStep userId: {}", userId);

        realtimeStepRankingRedisService.saveUserDailyStep(userId, step);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    @GetMapping("/getTest/accStepRanking")
    @Operation(summary = "redis 사용자 순위 조회 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> getRedisAccRankingInfo(@RequestParam("userId") int userId) {
        log.info("redis 사용자 순위 조회 테스트 - redis test getRedisAccRankingInfo");

        System.out.println("redis controller 테스트");
        int rank = accStepRankingRedisService.getUserRanking(1);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rank);
    }

    @GetMapping("/getTest/top10")
    @Operation(summary = "redis 누적 랭킹 Top 10 출력 테스트", description = "")
    public ResponseEntity<CommonResponseEntity> getRedisAccRankingTop10() {
        log.info("redis 누적 랭킹 Top 10 출력 테스트 - redis test getRedisAccRankingTop10");

        System.out.println("테스트");
        RedisStepRankingResponse accStepRankingList = accStepRankingRedisService.getRedisAccStepRankingList(0, 9);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, accStepRankingList);
    }

    @GetMapping("/person/top10Test")
    @Operation(summary = "개인 랭킹 top 10 조회", description = "어제/누적/실시간 개인 랭킹 정보를 top 10 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 개인 랭킹 top 10 조회 조회 성공", content = @Content(schema = @Schema(implementation = RedisStepRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getPersonalRankingTop10(@RequestParam("value") String value, @RequestParam("start") Integer start, @RequestParam("end") Integer end) {
        log.info("개인 걸음수 랭킹 top 10 조회 getPersonalRankingTop10 - value: {}", value);

//        RedisStepRankingResponse accStepRankingList;
        StepRankingResponse accStepRankingList;
        if (value.equals("yesterday")) {
            accStepRankingList = rankingService.getYesterdayStepRankingTop10();
//            accStepRankingList = yesterdayStepRankingRedisService.getRedisYesterdayStepRankingList(0, 9);
        } else if (value.equals("accumulation")) {
            accStepRankingList = rankingService.getAccStepRankingTop10();
//            accStepRankingList = accStepRankingRedisService.getRedisAccStepRankingList(0, 9);
        } else {
            RedisStepRankingResponse redisRealtimeStepRankingList = realtimeStepRankingRedisService.getRedisRealtimeStepRankingList(start, end);
            return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, redisRealtimeStepRankingList);
        }

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, accStepRankingList);
    }

    // 해당 사용자 redis에서 step 값 원하는 값으로 변경
    @GetMapping("/resetStep")
    public ResponseEntity<CommonResponseEntity> resetStepByUserId(@RequestParam("userId") int userId, @RequestParam("step") int step) {
        log.info("해당 사용자 redis에서 step 값 원하는 값으로 변경 userId: {}", userId);
        realtimeStepRankingRedisService.saveUserDailyStep(userId, step);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS);
    }

    // 해당 사용자 redis에서 userId에 대한 값 가져오기
    @GetMapping("/getUser")
    public ResponseEntity<CommonResponseEntity> getUserInfo(@RequestParam("userId") int userId) {
        log.info("해당 사용자 redis에서 userId에 대한 값 가져오기 userId: {}", userId);
        Map<String, String> dailyStepAndUser = realtimeStepRankingRedisService.getDailyStepAndUser(userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, dailyStepAndUser);
    }
}
