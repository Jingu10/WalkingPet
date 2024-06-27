package com.walkerholic.walkingpet.domain.ranking.dto.response;

import com.walkerholic.walkingpet.domain.ranking.dto.StepRankingList;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RedisStepRankingResponse {
    private List<StepRankingList> topRanking;

    public static RedisStepRankingResponse from(List<StepRankingList> accStepRanking) {
        return RedisStepRankingResponse.builder()
                .topRanking(accStepRanking)
                .build();
    }
}
