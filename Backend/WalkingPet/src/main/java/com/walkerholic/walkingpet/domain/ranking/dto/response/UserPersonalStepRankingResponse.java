package com.walkerholic.walkingpet.domain.ranking.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserPersonalStepRankingResponse {
    private int ranking;
    private int step;

    public static UserPersonalStepRankingResponse from(int ranking, int step) {
        return UserPersonalStepRankingResponse.builder()
                .ranking(ranking)
                .step(step)
                .build();
    }
}
