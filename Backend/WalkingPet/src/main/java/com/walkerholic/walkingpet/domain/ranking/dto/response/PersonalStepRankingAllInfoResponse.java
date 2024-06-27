package com.walkerholic.walkingpet.domain.ranking.dto.response;

import com.walkerholic.walkingpet.domain.ranking.dto.AccStepRankingAndUserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PersonalStepRankingAllInfoResponse {
    private UserPersonalStepRankingResponse myranking;
    private List<AccStepRankingAndUserInfo> rankings;

    public static PersonalStepRankingAllInfoResponse from(UserPersonalStepRankingResponse userPersonalStepRanking, List<AccStepRankingAndUserInfo> accStepRanking) {
        return PersonalStepRankingAllInfoResponse.builder()
                .myranking(userPersonalStepRanking)
                .rankings(accStepRanking)
                .build();
    }
}
