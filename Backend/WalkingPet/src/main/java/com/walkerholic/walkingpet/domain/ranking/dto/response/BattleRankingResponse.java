package com.walkerholic.walkingpet.domain.ranking.dto.response;

import com.walkerholic.walkingpet.domain.ranking.dto.BattleRankingList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// redis 캐싱에도 사용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BattleRankingResponse {
    private List<BattleRankingList> topRanking;

    @Builder
    public BattleRankingResponse(List<BattleRankingList> topRanking) {
        this.topRanking = topRanking;
    }

    public static BattleRankingResponse from(List<BattleRankingList> accStepRanking) {
        return BattleRankingResponse.builder()
                .topRanking(accStepRanking)
                .build();
    }
}
