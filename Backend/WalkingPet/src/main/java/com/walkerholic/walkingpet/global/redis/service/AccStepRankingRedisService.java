package com.walkerholic.walkingpet.global.redis.service;

import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingAndUserInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.StepRankingList;
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
public class AccStepRankingRedisService {
//    private static final String USERS_KEY = "USER_ACC_STEP_RANKING_INFO";
//    private static final String RANKING_KEY = "RANKING";
    private static final String STEP_KEY = "USER_ACC_STEP_RANKING_INFO";

    @Resource(name = "redisTemplateForStepCount")
    private final RedisTemplate<String, Integer> rankigRedisTemplate;

    private final UserInfoRedisService userInfoRedisService;

    // redis에 사용자들의 누적 걸음수 정보 저장
    public void saveAccStepList(AccStepRankingAndUserInfo accStepInfo) {
        rankigRedisTemplate.opsForZSet().add(STEP_KEY, accStepInfo.getUserId(), accStepInfo.getStep());
    }

    public void saveAccStep(AccStepRankingInfo accStepInfo) {
        rankigRedisTemplate.opsForZSet().add(STEP_KEY, accStepInfo.getUserId(), accStepInfo.getAccStep());
    }

    public void saveAccStep(int userId, int accStepInfo) {
        rankigRedisTemplate.opsForZSet().add(STEP_KEY, userId, accStepInfo);
    }

    public RedisStepRankingResponse getRedisAccStepRankingList(int startRanking, int endRanking) {
        List<StepRankingList> accStepRankingList = new ArrayList<>();
        Set<Integer> top10users = rankigRedisTemplate.opsForZSet().reverseRange(STEP_KEY, startRanking, endRanking);
        assert top10users != null;
        for (Integer userId: top10users) {
            UserRedisDto user = userInfoRedisService.getUser(userId);

            Double dStep = rankigRedisTemplate.opsForZSet().score(STEP_KEY, userId);
            int step = dStep != null ? dStep.intValue() : 0;

            int userRanking = getUserRanking(userId);
            accStepRankingList.add(StepRankingList.from(user, step, userRanking));
        }

        return RedisStepRankingResponse.from(accStepRankingList);
    }

    public StepRankingList getUserAccStepRanking(int userId) {
//        AccStepRankingAndUserInfo userStepInfo = getUser(userId);
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

    public int test(AccStepRankingAndUserInfo stepInfo) {
        Long userRanking = rankigRedisTemplate.opsForZSet().reverseRank(STEP_KEY, stepInfo.getUserId());

        return (userRanking != null) ? userRanking.intValue() + 1 : -1;
    }

    public List<AccStepRankingAndUserInfo> redisAccStepRankingToDto(Set<AccStepRankingAndUserInfo> accStepLankingList) {
        List<AccStepRankingAndUserInfo> accStepRankingList = new ArrayList<>();
        for (AccStepRankingAndUserInfo accStepRankingInfo : accStepLankingList) {
            accStepRankingList.add(AccStepRankingAndUserInfo.redisFrom(accStepRankingInfo));
        }
        return accStepRankingList;
    }
}
