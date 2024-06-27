package com.walkerholic.walkingpet.global.redis.service;

import com.walkerholic.walkingpet.domain.ranking.dto.UserInfoAndAllStepInfo;
import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import com.walkerholic.walkingpet.domain.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingRedisService {
    private final UserService userService;
    private final UserInfoRedisService userInfoRedisService;
    private final AccStepRankingRedisService accStepRankingRedisService;
    private final YesterdayStepRankingRedisService yesterdayStepRankingRedisService;
    private final RealtimeStepRankingRedisService realtimeStepRankingRedisService;
    private final BattleRankingRedisService battleRankingRedisService;

    /*
        redis 모든 랭킹 및 사용자 정보 저장
    */
    @Transactional(readOnly = false)
    public void saveRedisUserAndAllRanking() {

        List<UserInfoAndAllStepInfo> userAccStepAndInfoList = userService.getUserAccStepAndInfoList();
        for (UserInfoAndAllStepInfo info: userAccStepAndInfoList) {
            // 사용자 정보 저장
            userInfoRedisService.saveUser(
                    UserRedisDto.builder()
                            .userId(info.getUserId())
                            .nickname(info.getNickname())
                            .characterId(info.getCharacterId())
                            .build());

            // 누적 랭킹 저장
            accStepRankingRedisService.saveAccStep(info.getUserId(), info.getAccStep());

            // 실시간 걸음수 저장
            realtimeStepRankingRedisService.saveUserDailyStep(info.getUserId(), info.getDailyStep());

            // 어제 걸음수 저장
            yesterdayStepRankingRedisService.saveYesterdayStep(info.getUserId(), info.getYesterdayStep());

            // 배틀 점수 저장
            battleRankingRedisService.saveUserBattleScore(info.getUserId(), info.getBattleRating());
        }

//        // 사용자 정보 저장
//        List<UserRedisDto> allUserDetail = userService.getAllUserDetail();
//
//        for (UserRedisDto userRedisDto: allUserDetail) {
//            userInfoRedisService.saveUser(userRedisDto);
//        }
//
//        // 누적 랭킹 저장
//        List<AccStepRankingAndUserInfo> userAccStepList = rankingService.getUserAccStepAndInfoList();
//
//        for (AccStepRankingAndUserInfo stepInfo : userAccStepList) {
//            accStepRankingRedisService.saveAccStepList(stepInfo);
//        }
//
//        // 어제 걸음수 랭킹 저장
//        List<YesterdayStepRankingInfo> userYseterdayStepList = rankingService.getUserYseterdayStepList();
//
//        for (YesterdayStepRankingInfo stepInfo : userYseterdayStepList) {
//            yesterdayStepRankingRedisService.saveYesterdayStepList(stepInfo);
//        }
//
//        // 실시간 걸음수 랭킹 저장
//        List<ReailtimeStepRankingInfo> userRealtimeStepList = rankingService.getUserRealtimeStepList();
//
//        for (ReailtimeStepRankingInfo stepInfo : userRealtimeStepList) {
//            realtimeStepRankingRedisService.saveAllUserDailyStepList(stepInfo);
//        }
    }

    @Transactional(readOnly = false)
    public void saveRedisAllRanking() {

        List<UserInfoAndAllStepInfo> userAccStepAndInfoList = userService.getUserAccStepAndInfoList();
        for (UserInfoAndAllStepInfo info: userAccStepAndInfoList) {
            // 누적 랭킹 저장
            accStepRankingRedisService.saveAccStep(info.getUserId(), info.getAccStep());

            // 실시간 걸음수 저장
            realtimeStepRankingRedisService.saveUserDailyStep(info.getUserId(), info.getDailyStep());

            // 어제 걸음수 저장
            yesterdayStepRankingRedisService.saveYesterdayStep(info.getUserId(), info.getYesterdayStep());
        }
    }


}
