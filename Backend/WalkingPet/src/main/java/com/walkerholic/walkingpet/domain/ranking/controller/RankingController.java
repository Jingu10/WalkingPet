package com.walkerholic.walkingpet.domain.ranking.controller;

import com.walkerholic.walkingpet.domain.ranking.dto.BattleRankingList;
import com.walkerholic.walkingpet.domain.ranking.dto.StepRankingList;
import com.walkerholic.walkingpet.domain.ranking.dto.response.*;
import com.walkerholic.walkingpet.domain.ranking.service.RankingService;
import com.walkerholic.walkingpet.global.auth.dto.CustomUserDetail;
import com.walkerholic.walkingpet.global.error.GlobalSuccessCode;
import com.walkerholic.walkingpet.global.error.response.CommonResponseEntity;
import com.walkerholic.walkingpet.global.redis.service.AccStepRankingRedisService;
import com.walkerholic.walkingpet.global.redis.service.BattleRankingRedisService;
import com.walkerholic.walkingpet.global.redis.service.RealtimeStepRankingRedisService;
import com.walkerholic.walkingpet.global.redis.service.YesterdayStepRankingRedisService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/ranking")
public class RankingController {
    private final RankingService rankingService;
    private final AccStepRankingRedisService accStepRankingRedisService;
    private final YesterdayStepRankingRedisService yesterdayStepRankingRedisService;
    private final RealtimeStepRankingRedisService realtimeStepRankingRedisService;
    private final BattleRankingRedisService battleRankingRedisService;

    // mysql 버전으로 데이터 가져오기 -> 현재는 redis이기 때문에 사용x, 성능 테스트 확인용
    @GetMapping
    @Operation(summary = "유저의 개인 랭킹과 개인 랭킹 목록 조회", description = "유저의 어제/누적/실시간 랭킹 정보를 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 유저의 개인 랭킹과 개인 랭킹 목록 조회 성공", content = @Content(schema = @Schema(implementation = PersonalStepRankingAllInfoResponse.class)))
    public ResponseEntity<CommonResponseEntity> getPersonalRanking(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("value") String value) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("개인 랭킹 조회 getPersonalRanking - userId: {}, value: {}", userId, value);

        PersonalStepRankingAllInfoResponse personalStepRanking = rankingService.getAccStepRanking(userId);
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, personalStepRanking);
    }

    @GetMapping("/person/top10")
    @Operation(summary = "개인 랭킹 top 10 조회", description = "어제/누적/실시간 개인 랭킹 정보를 top 10 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 개인 랭킹 top 10 조회 조회 성공", content = @Content(schema = @Schema(implementation = RedisStepRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getPersonalRankingTop10(@RequestParam("value") String value) {
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
            RedisStepRankingResponse redisRealtimeStepRankingList = realtimeStepRankingRedisService.getRedisRealtimeStepRankingList(0, 9);
            return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, redisRealtimeStepRankingList);
        }

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, accStepRankingList);
    }

    @GetMapping("/person/top3")
    @Operation(summary = "개인 랭킹 top 3 조회", description = "어제/누적/실시간 개인 랭킹 정보를 top 3 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 개인 랭킹 top 3 조회 조회 성공", content = @Content(schema = @Schema(implementation = RedisStepRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getPersonalRankingTop3(@RequestParam("value") String value) {

        RedisStepRankingResponse accStepRankingList;
        if (value.equals("yesterday")) {
            log.info("개인 걸음수 랭킹 top 3 조회 getPersonalRankingTop3 - value: {}", "어제");
            return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getYesterdayStepRankingTop3());
//            accStepRankingList = yesterdayStepRankingRedisService.getRedisYesterdayStepRankingList(0, 2);
        } else if (value.equals("accumulation")) {
            log.info("개인 걸음수 랭킹 top 3 조회 getPersonalRankingTop3 - value: {}", "누적");
            return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getAccStepRankingTop3());
//            accStepRankingList = accStepRankingRedisService.getRedisAccStepRankingList(0, 2);
        } else {
            log.info("개인 걸음수 랭킹 top 3 조회 getPersonalRankingTop3 - value: {}", "실시간");
            return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, realtimeStepRankingRedisService.getRedisRealtimeStepRankingList(0, 2));
        }
    }

    @GetMapping("/person/myrank")
    @Operation(summary = "개인 랭킹 나의 순위 조회", description = "어제/누적/실시간 유저의 개인 랭킹 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 개인 랭킹 나의 순위 조회 성공", content = @Content(schema = @Schema(implementation = StepRankingList.class)))
    public ResponseEntity<CommonResponseEntity> getMyPersonalRanking(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestParam("value") String value) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("개인 랭킹 나의 순위 조회 getMyPersonalRanking - value: {}, userId: {}", value, userId);

        StepRankingList userAccStepRanking;
        if (value.equals("yesterday")) {
            userAccStepRanking = yesterdayStepRankingRedisService.getUserYesterdayStepRanking(userId);
        } else if (value.equals("accumulation")) {
            userAccStepRanking = accStepRankingRedisService.getUserAccStepRanking(userId);
        } else {
            userAccStepRanking = realtimeStepRankingRedisService.getUserRealtimeStepRanking(userId);
        }

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, userAccStepRanking);
    }

    @GetMapping("/group")
    @Operation(summary = "그룹 랭킹 top 10 조회", description = "그룹의 포인트를 기준으로 상위 10개 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 그룹 랭킹 top 10 조회 성공", content = @Content(schema = @Schema(implementation = TeamRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getTeamRankingTop10() {
        log.info("그룹 랭킹 top 10 조회 getTeamRankingTop10 ");

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getTeamRankingTop10());
    }

    @GetMapping("/myGroup")
    @Operation(summary = "나의 그룹 랭킹 조회", description = "나의 그룹의 포인트를 기준으로 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 나의 그룹 랭킹 조회 성공", content = @Content(schema = @Schema(implementation = TeamRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getMyTeamRanking(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("나의 그룹 랭킹 조회 getMyTeamRanking userId: {}", userId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getMyTeamRanking(userId));
    }

    @GetMapping("/group/count")
    @Operation(summary = "나의 그룹 수 조회", description = "내가 가입한 그룹의 수 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 나의 그룹 랭킹 조회 성공", content = @Content(schema = @Schema(implementation = TeamRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getMyGroupCount(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("나의 그룹 수 조회 getMyGroupCount userId: {}", userId);

        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getMyGroupCount(userId));
    }

    @GetMapping("/battle/top10")
    @Operation(summary = "배틀 랭킹 top 10 조회", description = "배틀 랭킹 정보를 top 10 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 배틀 랭킹 top 10 조회 조회 성공", content = @Content(schema = @Schema(implementation = BattleRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getBattleRankingTop10() {
        log.info("배틀 랭킹 top 10 조회 getBattleRankingTop10");

//        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getBattleRankingTop10());
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, battleRankingRedisService.getRedisBattleRankingList(0, 9));
    }

    @GetMapping("/battle/top3")
    @Operation(summary = "배틀 랭킹 top 3 조회", description = "배틀 랭킹 정보를 top 3 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 배틀 랭킹 top 3 조회 조회 성공", content = @Content(schema = @Schema(implementation = BattleRankingResponse.class)))
    public ResponseEntity<CommonResponseEntity> getBattleRankingTop3() {
            log.info("배틀 걸음수 랭킹 top 3 조회 getBattleRankingTop3");

//            return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getBattleRankingTop3());
            return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, battleRankingRedisService.getRedisBattleRankingList(0, 2));
    }

    @GetMapping("/battle/myrank")
    @Operation(summary = "배틀 랭킹 나의 순위 조회", description = "배틀 랭킹에서 나의 순위 가져오기")
    @ApiResponse(responseCode = "200", description = "S200 - 배틀 랭킹 나의 순위 조회 성공", content = @Content(schema = @Schema(implementation = BattleRankingList.class)))
    public ResponseEntity<CommonResponseEntity> getMyBattleRanking(@AuthenticationPrincipal CustomUserDetail userDetail) {
        Integer userId = userDetail.getUsers().getUserId();
        log.info("배틀 랭킹 나의 순위 조회 getMyBattleRanking - userId: {}", userId);

//        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, rankingService.getBattleRankingMyRank(userId));
        return CommonResponseEntity.toResponseEntity(GlobalSuccessCode.SUCCESS, battleRankingRedisService.getUserBattleRanking(userId));
    }
}
