package com.walkerholic.walkingpet.global.redis.service;

import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingAndUserInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.ReailtimeStepRankingInfo;
import com.walkerholic.walkingpet.domain.ranking.dto.StepRankingList;
import com.walkerholic.walkingpet.domain.ranking.dto.request.RealtimeStepRequest;
import com.walkerholic.walkingpet.domain.ranking.dto.response.RedisStepRankingResponse;
import com.walkerholic.walkingpet.domain.users.dto.UserRedisDto;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RealtimeStepRankingRedisService {
    private static final String STEP_KEY = "USER_REALTIME_STEP_RANKING_INFO";

    @Resource(name = "redisTemplateForStepCount")
    private final RedisTemplate<String, Integer> rankigRedisTemplate;

    private final UserStepRepository userStepRepository;
    private final UserInfoRedisService userInfoRedisService;

    // redis에 모든 사용자들의 실시간 걸음수 정보 저장
    @Transactional(readOnly = false)
    public void saveAllUserDailyStepList(ReailtimeStepRankingInfo rankingInfo) {
        //TODO: 한번에 저장하는 방식으로 변경
        rankigRedisTemplate.opsForZSet().add(STEP_KEY, rankingInfo.getUserId(), rankingInfo.getRealtimeStep());
    }

    @Transactional(readOnly = false)
    public void saveUserDailyStep(int userId, int realtimeStep) {
        rankigRedisTemplate.opsForZSet().add(STEP_KEY, userId, realtimeStep);
    }

    // 실시간 걸음수 기준 상위 랭킹 리스트 가져오기
    @Transactional(readOnly = true)
    public RedisStepRankingResponse getRedisRealtimeStepRankingList(int startRanking, int endRanking) {
        System.out.println("실시간 걸음수 redis 접근");
        List<StepRankingList> accStepRankingList = new ArrayList<>();
        Set<Integer> top10users = rankigRedisTemplate.opsForZSet().reverseRange(STEP_KEY, startRanking, endRanking);
        assert top10users != null;

        int userRanking = 0;
        int previousStep = -1;
        int sameRankCount = 0;
        for (Integer userId: top10users) {
//            AccStepRankingAndUserInfo userStepInfo = getUser(userId);
            System.out.println("redis userId: " + userId);
            UserRedisDto user = userInfoRedisService.getUser(userId);

            Double dStep = rankigRedisTemplate.opsForZSet().score(STEP_KEY, userId);
            int curStep = dStep != null ? dStep.intValue() : 0;

            if (curStep != previousStep) {
                userRanking += sameRankCount + 1;
                sameRankCount = 0;
            } else {
                sameRankCount++;
            }

            accStepRankingList.add(StepRankingList.from(user, curStep, userRanking));
            previousStep = curStep;
        }

        return RedisStepRankingResponse.from(accStepRankingList);
    }

    // 실시간 걸음수를 기준으로 특정 유저의 순위를 가져오기
    @Transactional(readOnly = true)
    public StepRankingList getUserRealtimeStepRanking(int userId) {
        UserRedisDto user = userInfoRedisService.getUser(userId);
//        AccStepRankingAndUserInfo userStepInfo = getUser(userId);

        Double dStep = rankigRedisTemplate.opsForZSet().score(STEP_KEY, userId);
        int step = dStep != null ? dStep.intValue() : 0;

        int userRanking = getUserRanking(userId, step);
        return StepRankingList.from(user, step, userRanking);
    }

    // 사용자의 실시간 걸음수 가져오기
    @Transactional(readOnly = true)
    public int getUserDailyStep(int userId) {
        Double dStep = rankigRedisTemplate.opsForZSet().score(STEP_KEY, userId);
        int step = dStep != null ? dStep.intValue() : 0;
        return step;
    }

//    @Transactional(readOnly = true)
//    public int getUserRanking(int userId) {
//        Long userRanking = rankigRedisTemplate.opsForZSet().reverseRank(STEP_KEY, userId);
//
//        return (userRanking != null) ? userRanking.intValue() + 1 : -1;
//    }
    @Transactional(readOnly = true)
    public int getUserRanking(int userId, int step) {
        // 해당 사용자 점수보다 높은 점수를 가진 사용자 수를 계산
        Long rank = rankigRedisTemplate.opsForZSet().count(STEP_KEY, (double) step + 1, Double.MAX_VALUE);

        if (rank == null) {
            return -1;
        }

        return rank.intValue() + 1;
    }

    @Transactional(readOnly = false)
    public void updateUserStepInfo() {
        Set<ZSetOperations.TypedTuple<Integer>> rankingData = rankigRedisTemplate.opsForZSet()
                .rangeByScoreWithScores(STEP_KEY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (ZSetOperations.TypedTuple<Integer> tuple : rankingData) {
            String userId = String.valueOf(tuple.getValue()); // userId 가져오기
            double dailyStep = tuple.getScore() == null ? 0 : tuple.getScore(); // step 가져오기

            UserStep userStep = userStepRepository.findById(Integer.valueOf(userId)).orElse(null);
            if (userStep != null) {
                userStep.updateUserStepInfo((int) dailyStep);
                userStepRepository.save(userStep);
            }
        }
    }

    @Transactional(readOnly = false)
    public void saveUserDailyStep() {
        Set<ZSetOperations.TypedTuple<Integer>> rankingData = rankigRedisTemplate.opsForZSet()
                .rangeByScoreWithScores(STEP_KEY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        for (ZSetOperations.TypedTuple<Integer> tuple : rankingData) {
            String userId = String.valueOf(tuple.getValue()); // userId 가져오기
            double dailyStep = tuple.getScore() == null ? 0 : tuple.getScore(); // step 가져오기

            UserStep userStep = userStepRepository.findById(Integer.valueOf(userId)).orElse(null);
            if (userStep != null) {
                userStep.updateDailyStep((int) dailyStep);
                userStepRepository.save(userStep);
            }
        }
    }

    @Transactional(readOnly = true)
    public Map<String, String> getDailyStepAndUser(int userId) {
        Map<String, String> result = new HashMap<>();

        Double dStep = rankigRedisTemplate.opsForZSet().score(STEP_KEY, userId);
        int step = dStep != null ? dStep.intValue() : 0;
        UserRedisDto user = userInfoRedisService.getUser(userId);
        result.put("userId", String.valueOf(user.getUserId()));
        result.put("nickname", String.valueOf(user.getNickname()));
        result.put("characterId", String.valueOf(user.getCharacterId()));
        result.put("step", String.valueOf(step));

        return result;
    }
}
