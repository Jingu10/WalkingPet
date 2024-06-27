package com.walkerholic.walkingpet.domain.ranking.dto.response;

import com.walkerholic.walkingpet.domain.ranking.dto.AccStepTop3Ranking;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class AccStepTop3RankingResponse {
    private List<AccStepTop3Ranking> topRanking;

    public static AccStepTop3RankingResponse from(List<AccStepTop3Ranking> accStepRanking) {
        return AccStepTop3RankingResponse.builder()
                .topRanking(accStepRanking)
                .build();
    }
}
