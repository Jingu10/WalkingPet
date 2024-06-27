package com.walkerholic.walkingpet.domain.ranking.dto;

import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccStepTop3Ranking {
    private int ranking;
    private String nickname; // user
    private int step; // user_step
    private int caracterId; // user_character

    public static AccStepTop3Ranking from(UserStep userStep) {
        return AccStepTop3Ranking.builder()
                .ranking(1)
                .nickname("테스트 유저")
                .step(userStep.getDailyStep())
                .caracterId(1)
                .build();
    }
}


