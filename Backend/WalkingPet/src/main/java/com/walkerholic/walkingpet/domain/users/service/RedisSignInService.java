package com.walkerholic.walkingpet.domain.users.service;

import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingAndUserInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.YesterdayStepRankingInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.request.RealtimeStepRequest;
import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import com.walkerholic.walkingpet.global.redis.service.AccStepRankingRedisService;
import com.walkerholic.walkingpet.global.redis.service.RealtimeStepRankingRedisService;
import com.walkerholic.walkingpet.global.redis.service.UserInfoRedisService;
import com.walkerholic.walkingpet.global.redis.service.YesterdayStepRankingRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSignInService {
    private final UserInfoRedisService userInfoRedisService;
    private final AccStepRankingRedisService accStepRankingRedisService;
    private final RealtimeStepRankingRedisService realtimeStepRankingRedisService;
    private final YesterdayStepRankingRedisService yesterdayStepRankingRedisService;

    // 회원가입 시 사용자의 걸음수 정보와 사용자 정보를 redis에 세팅
    @Transactional(readOnly = false)
    public void saveRedisRankingAndUser(int userId, String nickname) {
        // 사용자 정보 저장
        userInfoRedisService.saveUser(
                UserRedisDto.builder()
                        .userId(userId)
                        .nickname(nickname)
                        .characterId(1)
                        .build()
        );

        // 누적 랭킹 정보 저장
        accStepRankingRedisService.saveAccStep(userId, 0);

        // 실시간 랭킹 정보 저장
        realtimeStepRankingRedisService.saveUserDailyStep(userId, 0);

        // 어제 랭킹 정보 저장
        yesterdayStepRankingRedisService.saveYesterdayStep(userId, 0);
    }
}
