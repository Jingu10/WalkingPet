package com.walkerholic.walkingpet.domain.ranking.dto.response;

import com.walkerholic.walkingpet.domain.ranking.dto.PersonalStepRankingInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StepRankingResponse {
    private List<PersonalStepRankingInfo> topRanking;

    @Builder
    public StepRankingResponse(List<PersonalStepRankingInfo> topRanking) {
        this.topRanking = topRanking;
    }

    public static StepRankingResponse from(List<PersonalStepRankingInfo> accStepRanking) {
        return StepRankingResponse.builder()
                .topRanking(accStepRanking)
                .build();
    }
}
