package com.walkerholic.walkingpet.global.scheduler;

import com.walkerholic.walkingpet.global.redis.service.RankingRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankingScheduler {
    private final RankingRedisService rankingRedisService;

    // 어제/누적 top10, top3 캐싱 제거
    @Scheduled(cron = "0 2 0 * * ?", zone = "Asia/Seoul")  // 초, 분, 시, 일, 월, 요일, 연도(생략 가능)
    @CacheEvict(value = {"accStepRankingTop10", "accStepRankingTop3", "yesterdayStepRankingTop10", "yesterdayStepRankingTop3", "teamTop10"}, allEntries = true)
    public void clearYesterdayAndAccRankingCacheAtMidnight() {
        log.info("오전 12시 2분 - 어제/누적 top10, top3 및 그룹 랭킹 캐싱 제거 완료");
    }

    // 배틀 랭킹 top10, top3, 유저 랭킹 캐싱 제거 battleRankingTop10 battleRankingTop3 battleMyRank
    @Scheduled(cron = "0 2 * * * ?", zone = "Asia/Seoul")  // 초, 분, 시, 일, 월, 요일, 연도(생략 가능)
    @CacheEvict(value = {"battleRankingTop10", "battleRankingTop3", "battleMyRank"}, allEntries = true)
    public void clearBattleRankingCacheAtMidnight() {
        log.info("매시간 2분 - 배틀 랭킹 캐싱 제거 완료");
    }

}
