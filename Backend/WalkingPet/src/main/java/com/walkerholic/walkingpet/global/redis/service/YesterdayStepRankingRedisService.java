package com.walkerholic.walkingpet.global.redis.service;

import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingAndUserInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.StepRankingList;
import com.walkerholic.walkingpet.domain.ranking.dto.YesterdayStepRankingInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.response.RedisStepRankingResponse;
import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class YesterdayStepRankingRedisService {
    private static final String STEP_KEY = "USER_YESTERDAY_STEP_RANKING_INFO";

    @Resource(name = "redisTemplateForStepCount")
    private final RedisTemplate<String, Integer> rankigRedisTemplate;

    private final UserInfoRedisService userInfoRedisService;


    // redis에 사용자들의 누적 걸음수 정보 저장
    public void saveYesterdayStepList(YesterdayStepRankingInfo stepRankingInfo) {
        rankigRedisTemplate.opsForZSet().add(STEP_KEY, stepRankingInfo.getUserId(), stepRankingInfo.getYesterdayStep());
    }

    public void saveYesterdayStep(int userId, int yesterdayStep) {
        rankigRedisTemplate.opsForZSet().add(STEP_KEY, userId, yesterdayStep);
    }

    public RedisStepRankingResponse getRedisYesterdayStepRankingList(int startRanking, int endRanking) {
        List<StepRankingList> accStepRankingList = new ArrayList<>();
        Set<Integer> top10users = rankigRedisTemplate.opsForZSet().reverseRange(STEP_KEY, startRanking, endRanking);
        assert top10users != null;
        for (Integer userId: top10users) {
            UserRedisDto user = userInfoRedisService.getUser(userId);
//            AccStepRankingAndUserInfo userStepInfo = getUser(userId);

            Double dStep = rankigRedisTemplate.opsForZSet().score(STEP_KEY, userId);
            int step = dStep != null ? dStep.intValue() : 0;

            int userRanking = getUserRanking(userId);
            accStepRankingList.add(StepRankingList.from(user, step, userRanking));
        }

        return RedisStepRankingResponse.from(accStepRankingList);
    }

    public StepRankingList getUserYesterdayStepRanking(int userId) {
        UserRedisDto user = userInfoRedisService.getUser(userId);

        Double dStep = rankigRedisTemplate.opsForZSet().score(STEP_KEY, userId);
        int step = dStep != null ? dStep.intValue() : 0;

        int userRanking = getUserRanking(userId);
        return StepRankingList.from(user, step, userRanking);
    }

//    public AccStepRankingAndUserInfo getUser(int userId) {
//        HashOperations<String, Integer, AccStepRankingAndUserInfo> hashOperations = rankigRedisTemplate.opsForHash();
//
//        return hashOperations.get(RANKING_KEY, userId);
//    }

    public int getUserRanking(int userId) {
        Long userRanking = rankigRedisTemplate.opsForZSet().reverseRank(STEP_KEY, userId);

        return (userRanking != null) ? userRanking.intValue() + 1 : -1;
    }

}
